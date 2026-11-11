#!/usr/bin/env python3
"""Build the bundled Room database from the MHGenDatabase `mhgu.db`.

Usage:
    python3 tools/build_database.py path/to/mhgu.db

The source database comes from gatheringhallstudios/MHGenDatabase (MIT License,
Copyright (c) 2014 DavianCorp), file `app/src/main/assets/databases/mhgu.db.zip`.

The output is `app/src/main/assets/databases/mhgu_codex.db.gz`. The Android build
un-gzips it into the APK as `assets/databases/mhgu_codex.db`, which Room opens via
`createFromAsset`. Every CREATE statement below mirrors what Room generates
for the entities in `feature/*/data/*Entities.kt`; CI checks this with
`tools/verify_room_schema.py` against the schema Room exports during the build.
"""

import gzip
import os
import re
import shutil
import sqlite3
import sys
import tempfile
from collections import defaultdict

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
OUT = os.path.join(ROOT, "app/src/main/assets/databases/mhgu_codex.db.gz")

# Must equal `version` in MhguDatabase.
DB_VERSION = 2


def col(name, kind, nullable=False):
    return f"`{name}` {kind}" + ("" if nullable else " NOT NULL")


TABLES = {
    "monsters": (
        ["id INTEGER", "name TEXT", "type TEXT", "isLarge INTEGER", "iconRes TEXT?", "description TEXT"],
        ["id"], [],
    ),
    "monster_weakness": (
        ["monsterId INTEGER", "kind TEXT", "name TEXT", "value INTEGER"],
        ["monsterId", "kind", "name"], [],
    ),
    "monster_hitzones": (
        ["id INTEGER", "monsterId INTEGER", "part TEXT", "slash INTEGER", "impact INTEGER", "shot INTEGER",
         "fire INTEGER", "water INTEGER", "thunder INTEGER", "ice INTEGER", "dragon INTEGER"],
        ["id"], ["monsterId"],
    ),
    "monster_drops": (
        ["id INTEGER", "monsterId INTEGER", "itemName TEXT", "rank TEXT", "method TEXT", "percent INTEGER"],
        ["id"], ["monsterId"],
    ),
    "monster_habitats": (
        ["id INTEGER", "monsterId INTEGER", "location TEXT", "areas TEXT"],
        ["id"], ["monsterId"],
    ),
    "weapons": (
        ["id INTEGER", "name TEXT", "type TEXT", "treeName TEXT", "upgradeFrom TEXT?", "attack INTEGER",
         "affinity INTEGER", "element TEXT", "elementValue INTEGER", "sharpness TEXT", "special TEXT",
         "slots TEXT", "rarity INTEGER", "description TEXT"],
        ["id"], [],
    ),
    "armor": (
        ["id INTEGER", "name TEXT", "setName TEXT", "piece TEXT", "defense INTEGER", "maxDefense INTEGER",
         "fireRes INTEGER", "waterRes INTEGER", "thunderRes INTEGER", "iceRes INTEGER", "dragonRes INTEGER",
         "slots TEXT", "rarity INTEGER", "description TEXT"],
        ["id"], [],
    ),
    "armor_skills": (
        ["armorId INTEGER", "skillName TEXT", "points INTEGER"],
        ["armorId", "skillName"], [],
    ),
    "items": (
        ["id INTEGER", "name TEXT", "category TEXT", "rarity INTEGER", "description TEXT",
         "maxCarry INTEGER", "buyPrice INTEGER", "sellPrice INTEGER"],
        ["id"], [],
    ),
    "item_sources": (
        ["id INTEGER", "itemId INTEGER", "source TEXT", "detail TEXT"],
        ["id"], ["itemId"],
    ),
    "item_usages": (
        ["id INTEGER", "itemId INTEGER", "usage TEXT"],
        ["id"], ["itemId"],
    ),
    "quests": (
        ["id INTEGER", "name TEXT", "type TEXT", "rank TEXT", "objective TEXT", "locationName TEXT",
         "timeLimit INTEGER", "rewardZenny INTEGER", "description TEXT"],
        ["id"], [],
    ),
    "quest_monsters": (
        ["questId INTEGER", "monsterName TEXT"],
        ["questId", "monsterName"], [],
    ),
    "quest_rewards": (
        ["id INTEGER", "questId INTEGER", "itemName TEXT", "slot TEXT", "percent INTEGER"],
        ["id"], ["questId"],
    ),
    "skills": (
        ["id INTEGER", "name TEXT", "treeName TEXT", "category TEXT", "maxPoints INTEGER", "description TEXT"],
        ["id"], [],
    ),
    "skill_levels": (
        ["id INTEGER", "skillId INTEGER", "points INTEGER", "effect TEXT"],
        ["id"], ["skillId"],
    ),
    "skill_armor_sources": (
        ["skillId INTEGER", "armorName TEXT", "points INTEGER"],
        ["skillId", "armorName"], [],
    ),
    "skill_decoration_sources": (
        ["skillId INTEGER", "decorationName TEXT", "points INTEGER"],
        ["skillId", "decorationName"], [],
    ),
    "decorations": (
        ["id INTEGER", "name TEXT", "slotSize INTEGER", "rarity INTEGER", "obtain TEXT", "description TEXT"],
        ["id"], [],
    ),
    "decoration_skills": (
        ["decorationId INTEGER", "skillName TEXT", "points INTEGER"],
        ["decorationId", "skillName"], [],
    ),
    "locations": (
        ["id INTEGER", "name TEXT", "mapName TEXT", "terrain TEXT", "areaCount INTEGER", "description TEXT"],
        ["id"], [],
    ),
    "location_monsters": (
        ["locationId INTEGER", "monsterName TEXT"],
        ["locationId", "monsterName"], [],
    ),
    "gathering_points": (
        ["id INTEGER", "locationId INTEGER", "area TEXT", "itemName TEXT", "nodeType TEXT", "percent INTEGER"],
        ["id"], ["locationId"],
    ),
}


def columns(table):
    return [spec.split()[0] for spec in TABLES[table][0]]


def create_schema(db):
    for table, (specs, pk, indices) in TABLES.items():
        cols = []
        for spec in specs:
            name, kind = spec.split()
            cols.append(col(name, kind.rstrip("?"), nullable=kind.endswith("?")))
        pk_sql = ", ".join(f"`{c}`" for c in pk)
        db.execute(f"CREATE TABLE IF NOT EXISTS `{table}` ({', '.join(cols)}, PRIMARY KEY({pk_sql}))")
        for c in indices:
            db.execute(f"CREATE INDEX IF NOT EXISTS `index_{table}_{c}` ON `{table}` (`{c}`)")


def insert(db, table, rows):
    cols = columns(table)
    sql = f"INSERT INTO `{table}` ({', '.join(cols)}) VALUES ({', '.join('?' * len(cols))})"
    db.executemany(sql, [tuple(r[c] for c in cols) for r in rows])
    print(f"  {table}: {len(rows)}")


def dedupe(rows, key):
    seen, out = set(), []
    for r in rows:
        k = tuple(r[c] for c in key)
        if k not in seen:
            seen.add(k)
            out.append(r)
    return out


def slots_label(n):
    n = n or 0
    return "◯" * n + "—" * (3 - n)


def qty(name, n):
    return f"{name} ×{n}" if n and n > 1 else name


RANK_LABEL = {"LR": "Low Rank", "HR": "High Rank", "G": "G Rank"}
MONSTER_CLASS = {0: "Large monster", 1: "Small monster", 2: "Deviant"}
HUB_LABEL = {"Guild": "Hub"}
QUEST_KIND = {1: "Key quest", 2: "Urgent quest"}

CONSUMABLE_ICONS = {
    "icon_liquid", "icon_sac", "icon_ammo", "icon_bottle", "icon_meat", "icon_knife", "icon_trap",
    "icon_barrel_bomb", "icon_smoke", "icon_whetstone", "icon_flute", "icon_pickaxe", "icon_bugnet",
    "icon_nectar", "icon_dung", "icon_boomerang", "icon_binoculars", "icon_map_icon", "icon_traptool",
    "icon_spit", "icon_web", "icon_bait",
}
MATERIAL_ICONS = {
    "icon_scraps", "icon_carapace", "icon_fang", "icon_scale", "icon_shell", "icon_hide", "icon_bone",
    "icon_mantle", "icon_webbing", "icon_coin", "icon_bug", "icon_fish", "icon_herb", "icon_seed",
    "icon_mushroom", "icon_wood", "icon_husk", "icon_sprout", "icon_wyvern_crystal", "icon_jewel",
    "icon_egg", "icon_sphere", "icon_monster_jewel", "icon_question_mark",
}
TICKET_ICONS = {"icon_ticket", "icon_meal_ticket"}

TERRAIN = {
    "Jurassic Frontier": "Forest", "Verdant Hills": "Forest", "Misty Peaks": "Forest",
    "Deserted Island": "Forest", "Marshlands": "Forest", "Ancestral Steppe": "Forest",
    "Primal Forest": "Forest", "Jungle": "Forest",
    "Arctic Ridge": "Snow", "Frozen Seaway": "Snow", "Polar Field": "Snow",
    "Dunes": "Desert", "Desert": "Desert",
    "Volcano": "Volcano", "Volcanic Hollow": "Volcano",
    "Arena": "Arena", "V. Slayground": "Arena", "F. Slayground": "Arena", "Forlorn Arena": "Arena",
}


def item_category(row):
    if row["type"] == "Materials":
        return "Material"
    icon = row["icon_name"] or ""
    if icon == "icon_ore":
        return "Ore"
    if icon in CONSUMABLE_ICONS:
        return "Consumable"
    if icon in MATERIAL_ICONS:
        return "Material"
    if icon in TICKET_ICONS:
        return "Ticket"
    return "Other"


def build(src, db):
    q = lambda sql, *a: src.execute(sql, a).fetchall()
    item_name = {r["_id"]: r["name"] for r in q("SELECT _id, name FROM items")}
    item_type = {r["_id"]: r["type"] for r in q("SELECT _id, type FROM items")}
    monster_name = {r["_id"]: r["name"] for r in q("SELECT _id, name FROM monsters")}
    location_name = {r["_id"]: r["name"] for r in q("SELECT _id, name FROM locations")}
    tree_name = {r["_id"]: r["name"] for r in q("SELECT _id, name FROM skill_trees")}

    # --- monsters -------------------------------------------------------------------------
    ailments = defaultdict(list)
    for r in q("SELECT monster_id, ailment FROM monster_ailment ORDER BY _id"):
        ailments[r["monster_id"]].append(r["ailment"])
    monsters = []
    for r in q("SELECT * FROM monsters ORDER BY _id"):
        cls = int(r["class"])
        parts = []
        if r["base_hp"]:
            parts.append(f"Base HP {r['base_hp']}.")
        if ailments[r["_id"]]:
            parts.append("Inflicts: " + ", ".join(ailments[r["_id"]]) + ".")
        monsters.append(dict(
            id=r["_id"], name=r["name"], type=MONSTER_CLASS.get(cls, "Monster"),
            isLarge=0 if cls == 1 else 1, iconRes=None, description=" ".join(parts),
        ))
    insert(db, "monsters", monsters)

    weak_cols = [
        ("Element", ["fire", "water", "thunder", "ice", "dragon"]),
        ("Status", ["poison", "paralysis", "sleep"]),
        ("Item", ["pitfall_trap", "shock_trap", "flash_bomb", "sonic_bomb", "dung_bomb", "meat"]),
    ]
    weaknesses = []
    for r in q("SELECT * FROM monster_weakness ORDER BY _id"):
        if r["monster_id"] not in monster_name:
            continue
        for kind, cols in weak_cols:
            label = kind if r["state"] == "Normal" else f"{kind} · {r['state']}"
            for c in cols:
                weaknesses.append(dict(
                    monsterId=r["monster_id"], kind=label,
                    name=c.replace("_", " ").title(), value=r[c],
                ))
    insert(db, "monster_weakness", dedupe(weaknesses, ["monsterId", "kind", "name"]))

    insert(db, "monster_hitzones", [
        dict(id=r["_id"], monsterId=r["monster_id"], part=r["body_part"], slash=r["cut"] or 0,
             impact=r["impact"] or 0, shot=r["shot"] or 0, fire=r["fire"] or 0, water=r["water"] or 0,
             thunder=r["thunder"] or 0, ice=r["ice"] or 0, dragon=r["dragon"] or 0)
        for r in q("SELECT * FROM monster_damage ORDER BY _id")
    ])

    insert(db, "monster_drops", [
        dict(id=r["_id"], monsterId=r["monster_id"], itemName=qty(item_name[r["item_id"]], r["stack_size"]),
             rank=r["rank"], method=r["condition"], percent=r["percentage"])
        for r in q("SELECT * FROM hunting_rewards WHERE item_id IN (SELECT _id FROM items) ORDER BY _id")
    ])

    def habitat_areas(r):
        parts = []
        if r["start_area"] not in (None, ""):
            parts.append(f"Start {r['start_area']}")
        if r["move_area"]:
            parts.append(f"Moves {r['move_area']}")
        if r["rest_area"] not in (None, ""):
            parts.append(f"Rests {r['rest_area']}")
        return " · ".join(parts)

    habitats = q("SELECT * FROM monster_habitat ORDER BY _id")
    insert(db, "monster_habitats", [
        dict(id=r["_id"], monsterId=r["monster_id"], location=location_name[r["location_id"]],
             areas=habitat_areas(r))
        for r in habitats
    ])

    # --- weapons --------------------------------------------------------------------------
    weapon_rows = q("SELECT w.*, i.name, i.rarity, i.description FROM weapons w JOIN items i USING(_id) "
                    "ORDER BY w._id")
    parent = {r["_id"]: r["parent_id"] for r in weapon_rows}

    def root(wid):
        seen = set()
        while parent.get(wid) and parent[wid] in parent and wid not in seen:
            seen.add(wid)
            wid = parent[wid]
        return wid

    special_fields = [
        ("horn_notes", "Notes"), ("shelling_type", "Shelling"), ("phial", "Phial"),
        ("charges", "Charges"), ("coatings", "Coatings"), ("recoil", "Recoil"),
        ("reload_speed", "Reload"), ("rapid_fire", "Rapid fire"), ("deviation", "Deviation"),
        ("ammo", "Ammo"), ("special_ammo", "Special ammo"),
    ]
    weapons = []
    for r in weapon_rows:
        element, value = r["element"] or "", r["element_attack"] or 0
        extra = []
        if r["element_2"]:
            extra.append(f"Second element: {r['element_2']} {r['element_2_attack'] or 0}")
        if r["awaken"]:
            extra.append(f"Awaken: {r['awaken']} {r['awaken_attack'] or 0}")
            if not element:
                element, value = r["awaken"], r["awaken_attack"] or 0
        if r["defense"]:
            extra.append(f"Defense: +{r['defense']}")
        for field, label in special_fields:
            if r[field]:
                extra.append(f"{label}: {r[field]}")
        try:
            affinity = int(str(r["affinity"]).strip() or 0)
        except ValueError:
            affinity = 0
        weapons.append(dict(
            id=r["_id"], name=r["name"], type=r["wtype"], treeName=item_name[root(r["_id"])],
            upgradeFrom=item_name.get(r["parent_id"]) if r["parent_id"] else None,
            attack=r["attack"], affinity=affinity, element=element, elementValue=value,
            sharpness=r["sharpness"] or "", special="\n".join(extra),
            slots=slots_label(r["num_slots"]), rarity=r["rarity"], description=r["description"] or "",
        ))
    insert(db, "weapons", weapons)

    # --- armor ----------------------------------------------------------------------------
    families = {r["_id"]: r for r in q("SELECT * FROM armor_families")}
    hunter = {0: " (Blademaster)", 1: " (Gunner)"}
    armor_rows = q("SELECT a.*, i.name, i.rarity, i.description FROM armor a JOIN items i USING(_id) "
                   "ORDER BY a._id")
    armor_ids = {r["_id"] for r in armor_rows}
    armor = []
    for r in armor_rows:
        fam = families.get(r["family"])
        set_name = (fam["name"] + hunter.get(fam["hunter_type"], "")) if fam else r["name"]
        armor.append(dict(
            id=r["_id"], name=r["name"], setName=set_name, piece=r["slot"], defense=r["defense"],
            maxDefense=r["max_defense"] or r["defense"], fireRes=r["fire_res"], waterRes=r["water_res"],
            thunderRes=r["thunder_res"], iceRes=r["ice_res"], dragonRes=r["dragon_res"],
            slots=slots_label(r["num_slots"]), rarity=r["rarity"], description=r["description"] or "",
        ))
    insert(db, "armor", armor)

    item_skills = q("SELECT * FROM item_to_skill_tree ORDER BY _id")
    insert(db, "armor_skills", dedupe([
        dict(armorId=r["item_id"], skillName=tree_name[r["skill_tree_id"]], points=r["point_value"])
        for r in item_skills if r["item_id"] in armor_ids and r["skill_tree_id"] in tree_name
    ], ["armorId", "skillName"]))

    # --- decorations ----------------------------------------------------------------------
    components = q("SELECT * FROM components ORDER BY _id")
    recipe = defaultdict(list)
    for r in components:
        recipe[r["created_item_id"]].append(r)
    deco_rows = q("SELECT d._id, d.num_slots, i.name, i.rarity, i.description FROM decorations d "
                  "JOIN items i USING(_id) ORDER BY d._id")
    deco_ids = {r["_id"] for r in deco_rows}

    def recipe_text(created_id):
        by_type = defaultdict(list)
        for c in recipe[created_id]:
            by_type[c["type"] or "Create"].append(qty(item_name.get(c["component_item_id"], "?"), c["quantity"]))
        return "\n".join(f"{t}: " + ", ".join(parts) for t, parts in sorted(by_type.items()))

    insert(db, "decorations", [
        dict(id=r["_id"], name=r["name"], slotSize=r["num_slots"], rarity=r["rarity"],
             obtain=recipe_text(r["_id"]), description=r["description"] or "")
        for r in deco_rows
    ])
    insert(db, "decoration_skills", dedupe([
        dict(decorationId=r["item_id"], skillName=tree_name[r["skill_tree_id"]], points=r["point_value"])
        for r in item_skills if r["item_id"] in deco_ids and r["skill_tree_id"] in tree_name
    ], ["decorationId", "skillName"]))

    # --- skills ---------------------------------------------------------------------------
    levels_by_tree = defaultdict(list)
    for r in q("SELECT * FROM skills ORDER BY skill_tree_id, required_skill_tree_points DESC"):
        levels_by_tree[r["skill_tree_id"]].append(r)
    deco_trees = {r["skill_tree_id"] for r in item_skills if r["item_id"] in deco_ids}
    skills, skill_levels = [], []
    for tid, name in sorted(tree_name.items()):
        levels = levels_by_tree[tid]
        positive = [lv["name"] for lv in levels if lv["required_skill_tree_points"] > 0]
        skills.append(dict(
            id=tid, name=name, treeName=" / ".join(positive) or "No positive skills",
            category="Jewel" if tid in deco_trees else "Armor only",
            maxPoints=max((lv["required_skill_tree_points"] for lv in levels), default=0),
            description=f"{len(levels)} skill thresholds in the {name} tree.",
        ))
        for lv in levels:
            skill_levels.append(dict(
                id=lv["_id"], skillId=tid, points=lv["required_skill_tree_points"],
                effect=f"{lv['name']}: {lv['description']}",
            ))
    insert(db, "skills", skills)
    insert(db, "skill_levels", skill_levels)
    insert(db, "skill_armor_sources", dedupe([
        dict(skillId=r["skill_tree_id"], armorName=item_name[r["item_id"]], points=r["point_value"])
        for r in item_skills if r["item_id"] in armor_ids and r["skill_tree_id"] in tree_name
    ], ["skillId", "armorName"]))
    insert(db, "skill_decoration_sources", dedupe([
        dict(skillId=r["skill_tree_id"], decorationName=item_name[r["item_id"]], points=r["point_value"])
        for r in item_skills if r["item_id"] in deco_ids and r["skill_tree_id"] in tree_name
    ], ["skillId", "decorationName"]))

    # --- quests ---------------------------------------------------------------------------
    quest_rows = q("SELECT * FROM quests ORDER BY _id")
    quest_label = {}
    quests = []
    for r in quest_rows:
        hub = HUB_LABEL.get(r["hub"], r["hub"])
        quest_label[r["_id"]] = f"{r['name']} ({hub} {r['stars']}★)"
        extra = []
        if r["type"] in QUEST_KIND:
            extra.append(QUEST_KIND[r["type"]] + ".")
        if r["sub_goal"]:
            extra.append(f"Subquest: {r['sub_goal']} ({r['sub_reward'] or 0}z).")
        if r["fee"]:
            extra.append(f"Contract fee: {r['fee']}z.")
        if r["hrp"]:
            extra.append(f"HRP: {r['hrp']}.")
        quests.append(dict(
            id=r["_id"], name=r["name"], type=hub, rank=f"{r['rank']} {r['stars']}★",
            objective=r["goal"], locationName=location_name.get(r["location_id"], "Unknown"),
            timeLimit=r["time_limit"], rewardZenny=r["reward"],
            description="\n\n".join(filter(None, [r["flavor"] or "", " ".join(extra)])),
        ))
    insert(db, "quests", quests)

    quest_monsters = []
    for r in q("SELECT * FROM monster_to_quest ORDER BY _id"):
        name = monster_name.get(r["monster_id"])
        if name is None or r["quest_id"] not in quest_label:
            continue
        if r["hyper"]:
            name = f"Hyper {name}"
        quest_monsters.append(dict(questId=r["quest_id"], monsterName=name))
    insert(db, "quest_monsters", dedupe(quest_monsters, ["questId", "monsterName"]))

    reward_rows = q("SELECT * FROM quest_rewards ORDER BY _id")
    insert(db, "quest_rewards", [
        dict(id=r["_id"], questId=r["quest_id"], itemName=qty(item_name[r["item_id"]], r["stack_size"]),
             slot=r["reward_slot"], percent=r["percentage"])
        for r in reward_rows if r["item_id"] in item_name
    ])

    # --- locations ------------------------------------------------------------------------
    gathering = q("SELECT * FROM gathering ORDER BY _id")
    area_numbers = defaultdict(set)
    for r in gathering:
        m = re.match(r"Area (\d+)", r["area"])
        if m:
            area_numbers[r["location_id"]].add(int(m.group(1)))
    for r in habitats:
        for field in ("start_area", "move_area", "rest_area"):
            for n in re.findall(r"\d+", str(r[field] or "")):
                area_numbers[r["location_id"]].add(int(n))
    location_rows = q("SELECT * FROM locations ORDER BY _id")
    id_by_name = {r["name"]: r["_id"] for r in location_rows}
    locations = []
    for r in location_rows:
        base = r["name"].replace(" (N)", "")
        areas = area_numbers[r["_id"]] | area_numbers[id_by_name.get(base)]
        night = r["name"].endswith("(N)")
        locations.append(dict(
            id=r["_id"], name=r["name"], mapName=base, terrain=TERRAIN.get(base, "Special"),
            areaCount=max(areas, default=0),
            description="Night variant." if night else "",
        ))
    insert(db, "locations", locations)
    insert(db, "location_monsters", dedupe([
        dict(locationId=r["location_id"], monsterName=monster_name[r["monster_id"]])
        for r in habitats if r["monster_id"] in monster_name
    ], ["locationId", "monsterName"]))

    def node(r):
        flags = [r["site"], r["rank"]]
        if r["fixed"]:
            flags.append("fixed")
        if r["rare"]:
            flags.append("rare")
        return " · ".join(flags)

    insert(db, "gathering_points", [
        dict(id=r["_id"], locationId=r["location_id"], area=r["area"],
             itemName=qty(item_name[r["item_id"]], r["quantity"]), nodeType=node(r),
             percent=r["percentage"] or 0)
        for r in gathering if r["item_id"] in item_name
    ])

    # --- items ----------------------------------------------------------------------------
    item_rows = q("SELECT * FROM items WHERE type IN ('', 'Materials') ORDER BY _id")
    item_ids = {r["_id"] for r in item_rows}
    insert(db, "items", [
        dict(id=r["_id"], name=r["name"], category=item_category(r), rarity=r["rarity"],
             description=r["description"] or "", maxCarry=r["carry_capacity"] or 0,
             buyPrice=r["buy"] or 0, sellPrice=r["sell"] or 0)
        for r in item_rows
    ])

    sources = []

    def add_source(item_id, source, detail):
        if item_id in item_ids:
            sources.append(dict(id=len(sources) + 1, itemId=item_id, source=source, detail=detail))

    combining = q("SELECT * FROM combining ORDER BY _id")
    for r in combining:
        amount = (f"{r['amount_made_min']}" if r["amount_made_min"] == r["amount_made_max"]
                  else f"{r['amount_made_min']}-{r['amount_made_max']}")
        add_source(r["created_item_id"], "Combine",
                   f"{item_name[r['item_1_id']]} + {item_name[r['item_2_id']]} → ×{amount} ({r['percentage']}%)")
    for r in q("SELECT * FROM hunting_rewards ORDER BY monster_id, _id"):
        add_source(r["item_id"], "Monster",
                   f"{monster_name.get(r['monster_id'], '?')} · {RANK_LABEL.get(r['rank'], r['rank'])} · "
                   f"{r['condition']} · {r['percentage']}%")
    for r in gathering:
        add_source(r["item_id"], "Gathering",
                   f"{location_name.get(r['location_id'], '?')} · {r['area']} · {node(r)} · "
                   f"{r['percentage'] or 0}%")
    for r in reward_rows:
        if r["quest_id"] in quest_label:
            add_source(r["item_id"], "Quest reward",
                       f"{quest_label[r['quest_id']]} · {r['reward_slot']} · {r['percentage']}%")
    insert(db, "item_sources", sources)

    usages = []

    def add_usage(item_id, text):
        if item_id in item_ids:
            usages.append(dict(id=len(usages) + 1, itemId=item_id, usage=text))

    for r in combining:
        made = item_name[r["created_item_id"]]
        add_usage(r["item_1_id"], f"Combine with {item_name[r['item_2_id']]} → {made}")
        if r["item_2_id"] != r["item_1_id"]:
            add_usage(r["item_2_id"], f"Combine with {item_name[r['item_1_id']]} → {made}")
    verb = {"Create": "Craft", "Create B": "Craft (alt.)", "Improve": "Upgrade to"}
    for r in components:
        kind = item_type.get(r["created_item_id"]) or "Item"
        add_usage(r["component_item_id"],
                  f"{verb.get(r['type'], 'Craft')} {item_name.get(r['created_item_id'], '?')} "
                  f"({kind}) ×{r['quantity']}")
    insert(db, "item_usages", usages)


def main():
    if len(sys.argv) != 2:
        sys.exit(__doc__)
    src = sqlite3.connect(f"file:{sys.argv[1]}?mode=ro", uri=True)
    src.row_factory = sqlite3.Row
    with tempfile.TemporaryDirectory() as tmp:
        path = os.path.join(tmp, "mhgu_codex.db")
        db = sqlite3.connect(path)
        create_schema(db)
        build(src, db)
        db.execute(f"PRAGMA user_version = {DB_VERSION}")
        db.commit()
        db.execute("VACUUM")
        db.close()
        os.makedirs(os.path.dirname(OUT), exist_ok=True)
        with open(path, "rb") as f_in, gzip.GzipFile(OUT, "wb", compresslevel=9, mtime=0) as f_out:
            shutil.copyfileobj(f_in, f_out)
        print(f"raw {os.path.getsize(path)} bytes -> {OUT} {os.path.getsize(OUT)} bytes")


if __name__ == "__main__":
    main()

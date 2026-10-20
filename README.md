# Smart Pantry Manager

**A Java Android Application that Suggests Recipes Based Strictly on Leftover Ingredients to Cut Food Waste**

**Student:** Onthatile Ramokolo  
**Module:** Mobile App Development 700  
**Institution:** Richfield Graduate Institute of Technology  

---

## Description

Smart Pantry Manager helps users reduce food waste by tracking ingredients they already have at home and suggesting recipes they can cook using **strictly** those leftover ingredients. No shopping trip required — a recipe is only suggested when the user genuinely has every required ingredient in sufficient quantity.

## Features

- **Pantry Management** – Add, edit, and delete ingredients (name, quantity, unit, optional expiry date)
- **Strict Recipe Matching** – Only recipes where every ingredient is present in the required quantity appear in Suggested Recipes
- **18 Pre-loaded Recipes** – Seeded on first run
- **RecyclerView + Custom Adapters** – Dynamic lists for pantry and recipes
- **Bottom Navigation** – Pantry | Suggested | All Recipes | Settings
- **Input Validation** – Forms validate name and quantity before saving
- **Data Persistence** – SQLite (SQLiteOpenHelper) – data survives app restarts
- **Material Design UI** – Clean, modern, mobile-friendly interface

## Database Choice: SQLite

**Why SQLite?**
- Fully offline / on-device (no internet required)
- Aligns with the module’s persistent data chapter (SQLiteOpenHelper)
- Simple, reliable CRUD without external services
- Perfect for a personal pantry app that does not need cloud sync

## Database Choice: SQLite

This app uses SQLite with SQLiteOpenHelper because it is fully offline,
simple, and matches the persistent-data approach taught in the module.
## Screens (5+)

1. **Pantry List** (Fragment) – View / manage ingredients
2. **Add / Edit Ingredient** (Activity) – Form with validation
3. **Suggested Recipes** (Fragment) – Strict-matching results
4. **All Recipes** (Fragment) – Full recipe collection
5. **Recipe Detail** (Activity) – Ingredients + method
6. **Settings** (Fragment) – Expiry alerts toggle + units preference

## How to Run

1. Open **Android Studio** (Hedgehog or later recommended).
2. **File → Open** and select this project folder (`SmartPantryManager`).
3. Wait for Gradle sync to finish.
4. Connect a device or start an emulator (API 24+).
5. Click **Run** ▶.

### First Launch
- Database and 18 recipes are created automatically.
- Pantry starts empty — add ingredients to see suggested recipes appear.

## Strict-Matching Logic (Core Business Rule)

A recipe is suggested **only if**:
- Every single required ingredient exists in the pantry
- Available quantity ≥ required quantity

Matching is robust to:
- Case differences (`Tomato` vs `tomato`)
- Simple plural/singular (`tomatoes` → `tomato`, `eggs` → `egg`)

Partial matches are **never** shown in the Suggested list.

## Project Structure

```
app/src/main/java/com/richfield/smartpantry/
├── MainActivity.java
├── AddEditIngredientActivity.java
├── RecipeDetailActivity.java
├── SettingsActivity.java
├── adapter/
│   ├── PantryAdapter.java
│   └── RecipeAdapter.java
├── db/
│   └── DatabaseHelper.java      ← SQLite + seed + strict matching
├── fragment/
│   ├── PantryFragment.java
│   ├── SuggestedFragment.java
│   ├── RecipesFragment.java
│   └── SettingsFragment.java
└── model/
    ├── PantryItem.java
    ├── Recipe.java
    └── RecipeIngredient.java
```

## GitHub

Create a public GitHub repository, push this project, and make at least 10 meaningful commits during development (as required by the assignment).

Suggested commit sequence:
1. Initial project structure
2. DatabaseHelper + models
3. Pantry list + adapter
4. Add/Edit form + validation
5. Recipe seeding
6. Strict matching logic
7. Suggested Recipes screen
8. Recipe Detail
9. Settings + bottom navigation
10. UI polish + README

## Submission Notes

- Exclude `build/` and `.gradle/` folders when zipping for Moodle.
- Include a working GitHub link in your written report.
- Record the 5–7 minute video demonstrating CRUD + strict matching + code explanation.

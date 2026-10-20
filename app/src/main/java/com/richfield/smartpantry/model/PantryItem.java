package com.richfield.smartpantry.model;

public class PantryItem {
    private long id;
    private String name;
    private double quantity;
    private String unit;
    private String expiryDate; // yyyy-MM-dd or null

    public PantryItem() {}

    public PantryItem(String name, double quantity, String unit, String expiryDate) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public PantryItem(long id, String name, double quantity, String unit, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
        this.expiryDate = expiryDate;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getQuantity() { return quantity; }
    public void setQuantity(double quantity) { this.quantity = quantity; }

    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    /** Normalized name for matching (lowercase, trim, basic plural handling) */
    public String getNormalizedName() {
        return normalizeIngredientName(name);
    }

    public static String normalizeIngredientName(String name) {
        if (name == null) return "";
        String n = name.trim().toLowerCase();
        // Simple plural → singular
        if (n.endsWith("ies") && n.length() > 3) {
            n = n.substring(0, n.length() - 3) + "y";
        } else if (n.endsWith("oes") && n.length() > 3) {
            n = n.substring(0, n.length() - 2); // tomatoes → tomato
        } else if (n.endsWith("es") && n.length() > 2) {
            n = n.substring(0, n.length() - 2);
        } else if (n.endsWith("s") && !n.endsWith("ss") && n.length() > 1) {
            n = n.substring(0, n.length() - 1);
        }
        return n;
    }
}

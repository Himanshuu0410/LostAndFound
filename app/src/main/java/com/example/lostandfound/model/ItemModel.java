package com.example.lostandfound.model;

public class ItemModel {
    private int    id;
    private String type;
    private String itemName;
    private String category;
    private String personName;
    private String phone;
    private String address;
    private String description;
    private String imageUri;
    private String date;
    private String status;

    public ItemModel(int id, String type, String itemName, String category,
                     String personName, String phone, String address,
                     String description, String imageUri, String date, String status) {
        this.id          = id;
        this.type        = type;
        this.itemName    = itemName;
        this.category    = category;
        this.personName  = personName;
        this.phone       = phone;
        this.address     = address;
        this.description = description;
        this.imageUri    = imageUri;
        this.date        = date;
        this.status      = status;
    }

    public int    getId()          { return id; }
    public String getType()        { return type; }
    public String getItemName()    { return itemName; }
    public String getCategory()    { return category; }
    public String getPersonName()  { return personName; }
    public String getPhone()       { return phone; }
    public String getAddress()     { return address; }
    public String getDescription() { return description; }
    public String getImageUri()    { return imageUri; }
    public String getDate()        { return date; }
    public String getStatus()      { return status; }
    public boolean isLost()        { return "lost".equals(type); }
}

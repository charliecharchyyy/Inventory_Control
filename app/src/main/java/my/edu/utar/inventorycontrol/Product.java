package my.edu.utar.inventorycontrol;

public class Product {

    private String ProductName;

    private int ProductID;

    private int ProductAmount;

    private int ProductQuantity;

    private String ProductCategory;

    private String ProductDescription;

    public Product() {}

    public Product(String productName, int productID, int productAmount, int productQuantity, String productCategory, String productDescription) {
        ProductName = productName;
        ProductID = productID;
        ProductAmount = productAmount;
        ProductCategory = productCategory;
        ProductDescription = productDescription;
    }

//    public Product(String productName, int productID, int productAmount, String productCategory, String productDescription) {
//        ProductName = productName;
//        ProductID = productID;
//        ProductAmount = productAmount;
//        ProductCategory = productCategory;
//        ProductDescription = productDescription;
//    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public int getProductAmount() {
        return ProductAmount;
    }

    public void setProductAmount(int productAmount) {
        ProductAmount = productAmount;
    }

    public int getProductQuantity() {
        return ProductQuantity;
    }

    public void setProductQuantity(int productQuantity) {
        ProductQuantity = productQuantity;
    }

    public String getProductCategory() {
        return ProductCategory;
    }

    public void setProductCategory(String productCategory) {
        ProductCategory = productCategory;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }

    @Override
    public String toString() {
        return "\nProduct " + ProductID + "\n--------------\n" + "Name: " + ProductName + "\n" + "Price: RM " + ProductAmount + "\n" + "Category: "
                + ProductCategory + "\n" + "Description: " + ProductDescription + "\n";
    }
}



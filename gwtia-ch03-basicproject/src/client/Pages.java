package client;

public enum Pages {

    HOME(0, "Home"), PRODUCTS(1, "Products"), CONTACT(2, "Contact");

    // Holds the card number in the deck this enumeration relates to.
    private int val;
    // Holds the history token value this enumeration relates to.
    private String text;

    // Simple method to get the card number in the deck this enumeration relates to.
    int getVal(){return val;}
    // Simple method to get the history token this enumeration relates to.
    String getText(){return text;}

    // Enumeration constructor that stores the card number and history token for this enumeration.
    Pages(int val, String text) {
        this.val = val;
        this.text = text;
    };
}

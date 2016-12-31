/**
 * Add your package below. Package name can be found in the project's AndroidManifest.xml file.
 * This is the package name our example uses:
 *
 * package com.example.android.justjava; 
 */
package com.example.android.justjava2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {
    int quantity = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TextView Object test code snippet
        //TextView textView = new TextView(this);
        //textView.setText("This is text from a TextView Object");
        //textView.setTextSize(56);
        //textView.setTextColor(Color.BLUE);
        //setContentView(textView);
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        //display(quantity);
        //displayPrice(quantity * 5);
        int price = calculatePrice(quantity, 5);
        String priceMessage = "Total: $" + price + "\nThank you!";
        //displayMessage(priceMessage);
        displayMessage(createOrderSummary("Kaptain Kunal", quantity, 5));
    }

    private int calculatePrice(int quantity, int price4OneCoffee) {
        return quantity * price4OneCoffee;
    }

    private String createOrderSummary(String name, int quantity, int price4OneCoffee) {
        return ("Name: " + name + "\nQuantity: " + quantity + "\nTotal: $" + quantity * price4OneCoffee + "\nTank you!");
    }

    /**
     * This method displays the given quantity value on the screen.
     */
    private void display(int number) {
        TextView quantityTextView = (TextView) findViewById(R.id.quantity_text_view);
        quantityTextView.setText("" + number);
    }
    /**
     * This method displays the given price on the screen.
     */
    //private void displayPrice(int number) {
    //    TextView priceTextView = (TextView) findViewById(R.id.price_text_view);
    //    priceTextView.setText(NumberFormat.getCurrencyInstance().format(number));
    //}
    /**
     * This method displays the given text on the screen.
     */
    private void displayMessage(String message) {
        TextView orderSummaryTextView = (TextView) findViewById(R.id.order_summary_text_view);
        orderSummaryTextView.setText(message);
    }
    public void increment(View view) {
        ++quantity;
        display(quantity);
        //displayPrice(quantity * 5);
    }
    public void decrement(View view) {
        --quantity;
        display(quantity);
        //displayPrice(quantity * 5);
    }
}
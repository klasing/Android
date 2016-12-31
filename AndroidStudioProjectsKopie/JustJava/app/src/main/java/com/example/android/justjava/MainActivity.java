/**
 * Add your package below. Package name can be found in the project's AndroidManifest.xml file.
 * This is the package name our example uses:
 *
 * package com.example.android.justjava; 
 */
package com.example.android.justjava;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This app displays an order form to order coffee.
 */
public class MainActivity extends AppCompatActivity {
    int quantity = 1;
    int price4OneCoffee = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * This method is called when the order button is clicked.
     */
    public void submitOrder(View view) {
        EditText nameEditText = (EditText) findViewById(R.id.name_edit_text);
        String strName = nameEditText.getText().toString();
        CheckBox whippedCreamCheckBox = (CheckBox) findViewById(R.id.whipped_cream_checkbox);
        boolean hasWhippedCream = whippedCreamCheckBox.isChecked();
        //Log.v("MainActivity", "Has whipped cream: " + hasWhippedCream);
        CheckBox chocolateCheckBox = (CheckBox) findViewById(R.id.chocolate_checkbox);
        boolean hasChocolate = chocolateCheckBox.isChecked();
        //displayMessage(createOrderSummary(strName, hasWhippedCream, hasChocolate));
        String strOrderSummary = createOrderSummary(strName, hasWhippedCream, hasChocolate);
        Log.v("MainActivity", strOrderSummary);

        // start email app
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        //intent.putExtra(Intent.EXTRA_SUBJECT, "Just Java for " + strName);
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.just_java_for, strName));
        intent.putExtra(Intent.EXTRA_TEXT, strOrderSummary);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    private int calculatePrice(boolean hasWhippedCream, boolean hasChocolate) {
        if (hasWhippedCream) price4OneCoffee += 1;
        if (hasChocolate) price4OneCoffee += 2;
        return quantity * price4OneCoffee;
    }

    /**
     * Create summary of the order.
     *
     * @param b_whipped_cream is whether or not the user wants whipped cream topping
     * @param b_chocolate is whether or not the user wants whipped cream topping
     * @return text summary
     */
    private String createOrderSummary(String name, boolean b_whipped_cream, boolean b_chocolate) {
        String strReturn = getResources().getString(R.string.order_summary_name, name);
        //strReturn += "\n" + getResources().getString(R.string.add_whipped_cream, b_whipped_cream);
        if (b_whipped_cream) {
            strReturn += "\n" + getResources().getString(R.string.y_add_whipped_cream);
        } else {
            strReturn += "\n" + getResources().getString(R.string.n_add_whipped_cream);
        }
        //strReturn += "\n" + getResources().getString(R.string.add_chocolate, b_chocolate);
        if (b_chocolate) {
            strReturn += "\n" + getResources().getString(R.string.y_add_chocolate);
        } else {
            strReturn += "\n" + getResources().getString(R.string.n_add_chocolate);
        }
        strReturn += "\n" + getResources().getString(R.string.order_quantity, quantity);
        int total_price = calculatePrice(b_whipped_cream, b_chocolate);
        strReturn += "\n" + getResources().getString(R.string.order_total, total_price);
        strReturn += "\n" + getResources().getString(R.string.thank_you);
        return strReturn;
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
    //private void displayMessage(String message) {
    //    TextView priceTextView = (TextView) findViewById(R.id.price_text_view);
    //    priceTextView.setText(message);
    //}
    private void showToast(String strMessage) {
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, strMessage, duration);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();
    }
    public void increment(View view) {
        if (quantity == 100) {
            //showToast("You cannot have more than 100 coffees.");
            showToast(getResources().getString(R.string.not_more));
            return;
        }
        ++quantity;
        display(quantity);
        //displayPrice(quantity * 5);
    }
    public void decrement(View view) {
        if (quantity == 1) {
            //showToast("You cannot have less than 1 coffee.");
            showToast(getResources().getString(R.string.not_less));
            return;
        }
        --quantity;
        display(quantity);
        //displayPrice(quantity * 5);
    }
}
package glotech.smartgasapp;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

public class OrderStep1 extends AppCompatActivity {

    private MaterialButton compositeButton,traditionButton,button5kg,button16kg,button20kg,minusButton,plusButton,selectedToggleButton,cartButton;
    private MaterialButton addButton, nextButton;
    private TextView wordTextView;

    private boolean isCompositeSelected = false;
    private boolean isTraditionSelected = false;
    private int quantity = 0;
    public static ArrayList<OrderDetailItem> cartItemList = new ArrayList<OrderDetailItem>();
    private Dialog popDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_step1);

        compositeButton = findViewById(R.id.compositeButton);
        traditionButton = findViewById(R.id.traditionButton);
        button5kg = findViewById(R.id.button5kg);
        button16kg = findViewById(R.id.button16kg);
        button20kg = findViewById(R.id.button20kg);
        minusButton = findViewById(R.id.minusButton);
        plusButton = findViewById(R.id.plusButton);
        wordTextView = findViewById(R.id.wordTextView);
        addButton = findViewById(R.id.receipt_edit_button);
        nextButton = findViewById(R.id.receipt_next_button);
        cartButton = findViewById(R.id.shoppingCart);

        cartItemList = new ArrayList<OrderDetailItem>();

        // Set click listeners for the buttons
        compositeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCompositeSelected = !isCompositeSelected;
                if (isCompositeSelected) {
                    isTraditionSelected = false; // Deselect the other button
                }
                updateButtonState();
            }
        });

        traditionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isTraditionSelected = !isTraditionSelected;
                if (isTraditionSelected) {
                    isCompositeSelected = false; // Deselect the other button
                }
                updateButtonState();
            }
        });

        button5kg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateToggleButtonState(button5kg);
            }
        });

        button16kg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateToggleButtonState(button16kg);
            }
        });

        button20kg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateToggleButtonState(button20kg);
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    quantity--;
                    updateQuantity();
                }
            }
        });

        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                updateQuantity();
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCompositeSelected || isTraditionSelected) {
                    if(selectedToggleButton != null){
                        if (quantity>0) {
                            String selectedToggleButtonLabel = getToggleButtonLabel(selectedToggleButton);
                            String selectedType = isCompositeSelected ? "composite" : "tradition";
                            Toast.makeText(OrderStep1.this, "Added " + selectedToggleButtonLabel + " to cart for " + selectedType + quantity, Toast.LENGTH_SHORT).show();
                            OrderDetailItem od = new OrderDetailItem(String.valueOf(quantity), selectedType, selectedToggleButtonLabel,false);
                            cartItemList.add(od);
                            Log.i("cartItemList",cartItemList.toString());
                            resetButtonStates();
                        } else {
                            Toast.makeText(OrderStep1.this, "請選擇數量", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(OrderStep1.this, "請選擇瓦斯容量", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderStep1.this, "請選擇瓦斯桶類別", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isCompositeSelected || isTraditionSelected) {
                    if(selectedToggleButton != null){
                        if (quantity>0) {
                            String selectedToggleButtonLabel = getToggleButtonLabel(selectedToggleButton);
                            String selectedType = isCompositeSelected ? "Composite" : "Tradition";
                            Toast.makeText(OrderStep1.this, "Added " + selectedToggleButtonLabel + " to cart for " + selectedType + quantity, Toast.LENGTH_SHORT).show();
                            OrderDetailItem od = new OrderDetailItem(String.valueOf(quantity), selectedType, selectedToggleButtonLabel,false);
                            cartItemList.add(od);
                            resetButtonStates();
                            Intent intent = new Intent(OrderStep1.this, DeliveryMethod.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(OrderStep1.this, "請選擇數量", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else{
                        Toast.makeText(OrderStep1.this, "請選擇瓦斯容量", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(OrderStep1.this, "請選擇瓦斯桶類別", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });

        Button backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void updateButtonState() {
        // Update the state of the buttons based on the selection
        updateButtonState(compositeButton, isCompositeSelected);
        updateButtonState(traditionButton, isTraditionSelected);
    }

    private void updateButtonState(MaterialButton button, boolean isSelected) {
        // Change the border, icon color, and text color based on the selected state
        if (isSelected) {
            button.setStrokeColorResource(R.color.selected_color); // Set border color to black
            button.setIconTintResource(R.color.selected_color);    // Set icon color to black
            button.setTextColor(getResources().getColor(R.color.selected_color)); // Set text color to black
        } else {
            // Reset to default state if not selected
            button.setStrokeColorResource(R.color.non_selected_color); // Set default border color
            button.setIconTintResource(R.color.non_selected_color);    // Set default icon color
            button.setTextColor(getResources().getColor(R.color.non_selected_color)); // Set default text color
        }
    }
    private void updateToggleButtonState(MaterialButton button) {
        // Unselect the previously selected toggle button
        if (selectedToggleButton != null) {
            selectedToggleButton.setChecked(false);
            updateButtonState(selectedToggleButton, false);
        }

        // Select the clicked toggle button
        selectedToggleButton = button;
        selectedToggleButton.setChecked(true);
        updateButtonState(selectedToggleButton, true);
    }


    private void updateQuantity() {
        wordTextView.setText(String.valueOf(quantity));
    }

    private String getToggleButtonLabel(MaterialButton button) {
        if (button == button5kg) {
            return "5";
        } else if (button == button16kg) {
            return "16";
        } else if (button == button20kg) {
            return "20";
        } else {
            return "Unknown";
        }
    }

    private void resetButtonStates() {
        isCompositeSelected = false;
        isTraditionSelected = false;

        quantity = 0;
        updateQuantity();

        // Reset the state of toggle buttons
        if (selectedToggleButton != null) {
            selectedToggleButton.setChecked(false);
            updateButtonState(selectedToggleButton, false);
            selectedToggleButton = null;
        }

        // Update the UI to reflect the reset states
        updateButtonState();
    }

    private void showPopupWindow(View anchorView) {
        popDialog = new Dialog(this);
        popDialog.setContentView(R.layout.event_pop_up);
        // Access TextViews inside the popup layout
        TextView dissTextView = popDialog.findViewById(R.id.diss);
        TextView nameTextView = popDialog.findViewById(R.id.name);
        ListView listView = popDialog.findViewById(R.id.listview);

        // Set content for TextViews
        dissTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popDialog.dismiss();
            }
        });

        nameTextView.setText("購物車");
        OrderDetailListAdapter adapter = new OrderDetailListAdapter(getApplicationContext(), R.layout.adapter_order_detail_list, cartItemList);
        if (cartItemList.size() > 0) {
            // listView.setAdapter(null);
            // Stuff that updates the UI
            listView.setAdapter(adapter);
        }
        popDialog.show();
    }

}

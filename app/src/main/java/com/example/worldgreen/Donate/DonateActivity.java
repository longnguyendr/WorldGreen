package com.example.worldgreen.Donate;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.worldgreen.R;
import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;

import org.json.JSONException;

import java.math.BigDecimal;

public class DonateActivity extends AppCompatActivity {

    public static final int PAYPAL_REQUEST_CODE = 7171;
    private static final String TAG = "Donate Activity";

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(Config.PAYPAL_CLIENT_ID);

    Button BtnPayNow;
    EditText edtAmount;

    String amount = "1";

    TextView amountTextView;

    @Override
    protected void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate);

        //START PAYPAL SERVICE

        Intent intent = new Intent (this,PayPalService.class);
        intent.putExtra(PayPalService. EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        amountTextView = findViewById(R.id.amount_text_view);
        BtnPayNow = (Button)findViewById(R.id.BtnPayNow);
//        edtAmount = (EditText)findViewById(R.id.edtAmount);

        BtnPayNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                processPayment();
            }
        });

        setupSeekBar();
    }

    private void setupSeekBar() {
        SeekBar seekBar = findViewById(R.id.amount_seek_bar);
        int color = Color.parseColor("#D2D2D2");
        seekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amount = String.valueOf(progress);
                amountTextView.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void processPayment() {
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "EUR", "Donate for WorldGreen", PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent  = new Intent(this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, PAYPAL_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PAYPAL_REQUEST_CODE)
        {
            if (resultCode == RESULT_OK)
            {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);

                        startActivity(new Intent(this, PaymentDetails.class)
                                .putExtra("Payment Details", paymentDetails)
                                .putExtra("Payment Amount", amount));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            else if (resultCode == Activity.RESULT_CANCELED)
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show();
        }
        else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID)
            Toast.makeText(this, "Invalid", Toast.LENGTH_SHORT).show();

    }
}

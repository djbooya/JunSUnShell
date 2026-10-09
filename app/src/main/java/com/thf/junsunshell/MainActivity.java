package com.thf.junsunshell;

import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import android.os.*;
import android.util.Log;
import android.content.Context;
import android.widget.Toast;
import android.text.TextUtils;
import com.thf.junsunshell.databinding.ActivityMainBinding;
import com.thf.junsunshell.utils.Util;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private ActivityMainBinding binding;
    private Util util;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        // Inflate and get instance of binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // set content view to binding's root
        setContentView(binding.getRoot());
        context = getApplicationContext();
        util = new Util(getString(R.string.su));
    }

    public void execCommand(View view) {
        String command = binding.txtCommand.getText().toString();
        List<String> output;
        try {
            output = util.sudoForResult(command);
            binding.txtOutput.setText(TextUtils.join("\n", output));
        } catch (Util.SuCommandException ex) {
            binding.txtOutput.setText(ex.getMessage());
        }
    }
}

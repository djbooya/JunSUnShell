package com.thf.junsunshell;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.thf.junsunshell.databinding.ActivityMainBinding;
import com.thf.junsunshell.utils.CommandHistory;
import com.thf.junsunshell.utils.Util;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private Util util;
    private CommandHistory history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        util = new Util(getString(R.string.su));
        history = new CommandHistory(this);

        binding.btnRun.setOnClickListener(v -> showCommandDialog());
        binding.btnExit.setOnClickListener(v -> finishAndRemoveTask());

        // Open the command pop-up straight away on a fresh launch
        if (savedInstanceState == null) {
            showCommandDialog();
        }
    }

    private void showCommandDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_command, null);
        EditText txtCommand = view.findViewById(R.id.txtCommand);
        TextView txtHistoryEmpty = view.findViewById(R.id.txtHistoryEmpty);
        ListView listHistory = view.findViewById(R.id.listHistory);

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listHistory.setAdapter(adapter);
        Runnable refreshHistory = () -> {
            adapter.clear();
            adapter.addAll(history.getAll());
            boolean empty = adapter.isEmpty();
            txtHistoryEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
            listHistory.setVisibility(empty ? View.GONE : View.VISIBLE);
        };
        refreshHistory.run();

        listHistory.setOnItemClickListener((parent, item, position, id) -> {
            txtCommand.setText(adapter.getItem(position));
            txtCommand.setSelection(txtCommand.length());
            txtCommand.requestFocus();
        });
        listHistory.setOnItemLongClickListener((parent, item, position, id) -> {
            history.remove(adapter.getItem(position));
            refreshHistory.run();
            Toast.makeText(this, R.string.history_removed, Toast.LENGTH_SHORT).show();
            return true;
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.run_command)
                .setView(view)
                .setPositiveButton(R.string.run, null)
                .setNegativeButton(R.string.cancel, null)
                .setNeutralButton(R.string.clear_history, null)
                .create();

        Runnable run = () -> {
            String command = txtCommand.getText().toString().trim();
            if (command.isEmpty()) {
                txtCommand.setError(getString(R.string.command_empty));
                return;
            }
            dialog.dismiss();
            history.add(command);
            execCommand(command);
        };
        txtCommand.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_GO) {
                run.run();
                return true;
            }
            return false;
        });

        // Set the listeners after show() so Run and Clear history don't always close the dialog
        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> run.run());
            dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setOnClickListener(v -> {
                history.clear();
                refreshHistory.run();
            });
        });
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    private void execCommand(String command) {
        binding.txtLastCommand.setText("$ " + command);
        try {
            List<String> output = util.sudoForResult(command);
            binding.txtOutput.setText(output.isEmpty()
                    ? getString(R.string.no_output)
                    : TextUtils.join("\n", output));
        } catch (Util.SuCommandException ex) {
            binding.txtOutput.setText(ex.getMessage());
        }
        binding.scrollOutput.scrollTo(0, 0);
    }
}

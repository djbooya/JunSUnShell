package com.thf.junsunshell.utils;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.List;

/** Commands the user has run, newest first, saved across app restarts. */
public class CommandHistory {

    private static final String PREFS = "command_history";
    private static final String KEY = "commands";
    private static final int MAX_ENTRIES = 50;

    private final SharedPreferences prefs;

    public CommandHistory(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public List<String> getAll() {
        List<String> commands = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY, "[]"));
            for (int i = 0; i < array.length(); i++) {
                commands.add(array.getString(i));
            }
        } catch (JSONException ex) {
            // Corrupt history: start over
        }
        return commands;
    }

    /** Moves the command to the top, removing any earlier copy. */
    public void add(String command) {
        List<String> commands = getAll();
        commands.remove(command);
        commands.add(0, command);
        while (commands.size() > MAX_ENTRIES) {
            commands.remove(commands.size() - 1);
        }
        save(commands);
    }

    public void remove(String command) {
        List<String> commands = getAll();
        commands.remove(command);
        save(commands);
    }

    public void clear() {
        save(new ArrayList<>());
    }

    private void save(List<String> commands) {
        prefs.edit().putString(KEY, new JSONArray(commands).toString()).apply();
    }
}

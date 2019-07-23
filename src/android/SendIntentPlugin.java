package com.outsystems.sendintentplugin;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * The send intent external plugin implemented for Android.
 */
public class SendIntentPlugin extends CordovaPlugin {

    private static final String TAG = "SendIntentPlugin";
    private CallbackContext callbackContext;
    private static final int LOGIN_SUCCESS_CODE = 9001;

    private static final String ACTION = "action";
    private static final String INPUT_EXTRAS = "inputExtras";
    private static final String ACCESS_TOKEN = "access_token";

    /**
     * Executes the request and returns JSONObject
     *
     * @param action          The action to execute.
     * @param args            JSONArray used to call another application with some parameters.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return True when the action was valid, false otherwise.
     */
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        this.callbackContext = callbackContext;

        if (action != null) {

            switch (Actions.getActionByName(action)) {
                case SEND_INTENT:
                    sendIntent(args);
                    return true;
                case INVALID:
                    callbackContext.error(Actions.INVALID.getDescription());
                    return false;

            }
        } else {
            Log.v(TAG, Actions.INVALID.getDescription());
            callbackContext.error(Actions.INVALID.getDescription());
            return false;
        }

        return true;
    }

    /**
     * The sendIntent method in another Application
     *
     * @param args some args to use in another Application
     */
    private void sendIntent(JSONArray args) throws JSONException {
        JSONObject object = args.getJSONObject(0);

        String action = object.getString(ACTION);
        JSONArray inputExtras = object.getJSONArray(INPUT_EXTRAS);

        Intent intentLogin = new Intent();
        intentLogin.setAction(action.trim());
        intentLogin.setFlags(0);

        try {
            if (inputExtras != null && inputExtras.length() > 0) {
                for (int i = 0; i < inputExtras.length(); i++) {

                    JSONObject obj = inputExtras.getJSONObject(i);

                    String key = obj.getString("key");
                    String value = obj.getString("value");

                    intentLogin.putExtra(key, value);
                }
            }

            callAnotherApplication(intentLogin);

        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(e.getMessage());
        }
    }

    /**
     * Call another application using package name
     *
     * @param intentLogin the intent with some attributes
     */
    private void callAnotherApplication(Intent intentLogin) {
        if (this.cordova.getActivity().getPackageManager().resolveActivity(intentLogin, 0) != null) {
            this.cordova.startActivityForResult(this, intentLogin, LOGIN_SUCCESS_CODE);
        } else {
            callbackContext.error(Actions.INVALID.getDescription());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK || requestCode == LOGIN_SUCCESS_CODE) {
            if (intent.getExtras() != null && intent.getExtras().get(ACCESS_TOKEN) != null) {
                String accessToken = (String) intent.getExtras().get(ACCESS_TOKEN);
                if (accessToken != null) {
                    JSONObject response = new JSONObject();
                    try {
                        response.put(ACCESS_TOKEN, accessToken);
                        callbackContext.success(response);
                    } catch (JSONException e) {
                        Log.v(TAG, e.getMessage());
                        callbackContext.error(e.getMessage());
                    }
                } else {
                    callbackContext.error("Error to get the access_token value.");
                }
            } else {
                callbackContext.error("Error to get the access_token value.");
            }
        }
    }

    private enum Actions {

        SEND_INTENT("sendIntent", "The send intent action is used to execute action in another application."),
        INVALID("", "Invalid or not found action!");

        private String action;
        private String description;

        Actions(String action, String description) {
            this.action = action;
            this.description = description;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public static Actions getActionByName(String action) {
            for (Actions a : Actions.values()) {
                if (a.action.equalsIgnoreCase(action)) {
                    return a;
                }
            }
            return INVALID;
        }
    }
}
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
    private static final String DEVICE_ID = "device_id";
    private static final String ERROR_CODE_A = "500";
    private static final String ERROR_CODE_B = "404";

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
                    callbackContext.error(ERROR_CODE_A);
                    return false;

            }
        } else {
            Log.v(TAG, Actions.INVALID.getDescription());
            callbackContext.error(ERROR_CODE_A);
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
                    Type type = Type.getTypeByName(obj.getString("type"));

                    switch (type){
                        case BOOL:
                            intentLogin.putExtra(key,obj.getBoolean("value"));
                            break;
                        case LONG:
                            intentLogin.putExtra(key,obj.getLong("value"));
                            break;
                        case DOUBLE:
                            intentLogin.putExtra(key,obj.getDouble("value"));
                            break;
                        case STRING:
                            intentLogin.putExtra(key,obj.getString("value"));
                            break;
                        case INTEGER:
                            intentLogin.putExtra(key,obj.getInt("value"));
                            break;
                    }
                }
            }

            callAnotherApplication(intentLogin);

        } catch (JSONException e) {
            e.printStackTrace();
            callbackContext.error(ERROR_CODE_A);
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
            callbackContext.error(ERROR_CODE_A);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (resultCode == Activity.RESULT_OK) {
            if (intent.getExtras() != null && intent.getExtras().get(ACCESS_TOKEN) != null) {
                String accessToken = (String) intent.getExtras().get(ACCESS_TOKEN);
                if (accessToken != null) {
                    JSONObject response = new JSONObject();
                    try {
                        response.put(ACCESS_TOKEN, accessToken); 
                        System.out.println("accessToken: "+accessToken);
                        //check if also contain device id
                        if (intent.getExtras() != null && intent.getExtras().get(DEVICE_ID) != null) {
                            String devicId = (String) intent.getExtras().get(DEVICE_ID);
                            System.out.println("devicId: "+devicId);
                            if (devicId != null) {
                                try {
                                    response.put(DEVICE_ID, devicId);
                                    System.out.println("return accesstoken: "+accessToken+" and device ID: "+devicId);
                                    callbackContext.success(response); //return both Access token and device Id
                                } catch (JSONException e) {
                                    Log.v(TAG, e.getMessage());
                                    System.out.println("device id is NULL 3: "+e.getMessage());
                                    //callbackContext.error(ERROR_CODE_B);
                                    callbackContext.success(response); //only return access token
                                }
                            } else {
                            System.out.println("device id is NULL 2");
                                //callbackContext.error(ERROR_CODE_B);
                                callbackContext.success(response); //only return access token
                            }
                        } else {
                            System.out.println("device id is NULL 1");
                            //callbackContext.error(ERROR_CODE_B);
                            callbackContext.success(response); //only return access token
                        }
                    } catch (JSONException e) {
                        Log.v(TAG, e.getMessage());
                        callbackContext.error(ERROR_CODE_B);
                    }
                } else {
                    callbackContext.error(ERROR_CODE_B);
                }
            } else {
                callbackContext.error(ERROR_CODE_B);
            }
        } else {
            callbackContext.error(ERROR_CODE_B);
        }
    }

    private enum Actions {

        SEND_INTENT("sendIntent", ERROR_CODE_B),
        INVALID("", ERROR_CODE_A);

        private String action;
        private String description;

        Actions(String action, String description) {
            this.action = action;
            this.description = description;
        }

        public String getDescription() {
            return description;
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
    private enum Type {
        BOOL("bool"),
        LONG("long"),
        DOUBLE("double"),
        STRING("string"),
        INTEGER("int");

        private String action;

        Type(String action) {
            this.action = action;
        }

        public static Type getTypeByName(String action) {
            for (Type a : Type.values()) {
                if (a.action.equalsIgnoreCase(action)) {
                    return a;
                }
            }
            return STRING;
        }
    }
}
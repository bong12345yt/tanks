package com.tb.tanks.tankGame.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

public class GameState {
    private static final int RENDER_DELAY = 1;
    private ArrayList<JSONObject> gameUpdates = new ArrayList<JSONObject>();
    private long gameStart = 0;
    private long firstServerTimestamp = 0;

    public GameState() {
        gameStart = 0;
        firstServerTimestamp = 0;
    }

    public void processGameUpdate(JSONObject update) {
        if (firstServerTimestamp == 0) {
            try {
                firstServerTimestamp = update.getLong("time");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            gameStart = System.currentTimeMillis();
        }

        gameUpdates.add(update);

        // Keep only one game update before the current server time
        int base = getBaseUpdate();
        if (base > 0) {
            for (int i = 0; i < base; i++) {
                if (i < gameUpdates.size())
                    gameUpdates.remove(0);
            }

        }
    }

    public long currentServerTime() {
        return firstServerTimestamp + (System.currentTimeMillis() - gameStart) - RENDER_DELAY;
    }

    public int getBaseUpdate() {
        long serverTime = currentServerTime();
        for (int i = gameUpdates.size() - 1; i >= 0; i--) {
            try {
                if (gameUpdates.get(i).getLong("time") <= serverTime) {
                    return i;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return -1;
    }

    public JSONArray interpolateObjectArray(JSONArray objects1, JSONArray objects2, float ratio) {
        JSONArray result = new JSONArray();
        try {

            for (int i = 0; i < objects1.length(); i++) {
                result.put(interpolateObject(objects1.getJSONObject(i), objects2.getJSONObject(i), ratio));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject interpolateObject(JSONObject obj1, JSONObject obj2, float ratio) {
        if (obj2 == null) {
            return obj1;
        }
        JSONObject interpolated = new JSONObject();

        for (Iterator<String> it = obj1.keys(); it.hasNext(); ) {
            String key = it.next();
            try {
                Object objJSON1 = obj1.get(key);
                if (key == "degree") {
                    interpolated.put(key, interpolateDegree((float) obj1.getDouble(key), (float) obj2.getDouble(key), ratio));
                } else if (key == "id") {
                    continue;
                } else if (objJSON1 instanceof Integer) {
                    interpolated.put(key, (obj1.getInt(key) + (obj2.getInt(key) - obj1.getInt(key))));
                } else if (objJSON1 instanceof Double || objJSON1 instanceof Long) {
                    interpolated.put(key, (obj1.getDouble(key) + (obj2.getDouble(key) - obj1.getDouble(key))));
                } else if (objJSON1 instanceof Long) {
                    interpolated.put(key, (obj1.getLong(key) + (obj2.getLong(key) - obj1.getLong(key))));
                } else if (objJSON1 instanceof JSONArray) {
                    interpolated.put(key, interpolateObjectArray(obj1.getJSONArray(key), obj2.getJSONArray(key), ratio));
                } else if (objJSON1 instanceof Boolean) {
                    interpolated.put(key, objJSON1);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return interpolated;
    }

    public float interpolateDegree(float degree1, float degree2, float ratio) {
        float absD = Math.abs(degree1 - degree2);
        if (absD >= 180) {
            // The angle between the directions is large - we should rotate the other way
            if (degree1 > degree2) {
                return degree1 + (degree2 + 2 * 180 - degree1) * ratio;
            } else {
                return degree1 - (degree2 - 2 * 180 - degree1) * ratio;
            }
        } else {
            // Normal interp
            return degree1 + (degree2 - degree1) * ratio;
        }
    }


    public JSONObject getCurrentState() {
        if (firstServerTimestamp == 0) {
            return new JSONObject();
        }

        int base = getBaseUpdate();
        long serverTime = currentServerTime();
        int size = gameUpdates.size();
        if (base < 0 || base == size - 1) {
            return gameUpdates.get(size - 1);
        } else {
            try {
                JSONObject baseUpdate = gameUpdates.get(base);
                JSONObject next = gameUpdates.get(base + 1);
                long baseTime = baseUpdate.getLong("time");
                float ratio = (serverTime - baseTime) / (next.getLong("time") - baseTime);
                JSONObject toUpdate = new JSONObject();
                toUpdate.put("me", interpolateObject(baseUpdate.getJSONObject("me"), next.getJSONObject("me"), ratio));
                toUpdate.put("others", interpolateObjectArray(baseUpdate.getJSONArray("others"), next.getJSONArray("others"), ratio));
                return toUpdate;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

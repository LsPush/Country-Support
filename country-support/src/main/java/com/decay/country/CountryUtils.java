/*
 * Copyright 2017 LsPush
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.decay.country;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Locale;

public class CountryUtils {
    private static CountryUtils INSTANCE;
    private LinkedHashMap<String, Country> mCountries;
    private Resources mResources;
    private TelephonyManager mTelephonyManager;

    private CountryUtils(Context context) {
        mResources = context.getResources();
        mTelephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    }

    public static CountryUtils getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new CountryUtils(context);
        }
        return INSTANCE;
    }

    private void init() {
        synchronized (CountryUtils.class) {
            if (mCountries != null) return;

            String commonAreas = mResources.getString(R.string.common_areas);
            String[] arrays = mResources.getStringArray(R.array.countries);
            mCountries = new LinkedHashMap<>(arrays.length);
            for (String it : arrays) {
                Country country = new Country();
                String[] data = it.split(" ");

                country.group = data[data.length - 1];
                if (country.group.equals("*")) {
                    country.group = commonAreas;
                }
                country.phoneLength = data[data.length - 2];
                country.countryCode = data[data.length - 3];
                country.country = data[data.length - 4];
                if (data.length - 4 == 1) {
                    country.name = data[0];
                } else {
                    String[] array = new String[data.length - 4];
                    System.arraycopy(data, 0, array, 0, data.length - 4);
                    country.name = TextUtils.join(" ", array);
                }

                mCountries.put(country.country, country);
            }
        }
    }

    public Country getCurrentCountry() {
        String country = mTelephonyManager.getSimCountryIso();
        if (country == null || isSimulator()) {
            country = mTelephonyManager.getNetworkCountryIso();
            if (country == null) {
                country = Locale.getDefault().getCountry();
            }
        }
        if (TextUtils.isEmpty(country)) {
            country = "CN";
        }
        country = country.toUpperCase(Locale.ENGLISH);

        return getCountry(country);
    }

    public Country getCountry(String country) {
        init();
        return mCountries != null ? mCountries.get(country) : null;
    }

    public static boolean isVBox() {
        return Build.FINGERPRINT.contains("vbox");
    }

    public static boolean isSimulator() {
        return isVBox() || Build.FINGERPRINT.contains("generic");
    }

    public Collection<Country> getCountries() {
        init();
        return mCountries.values();
    }
}

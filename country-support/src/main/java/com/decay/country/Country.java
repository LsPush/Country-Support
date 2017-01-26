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

import android.telephony.TelephonyManager;

import java.util.Locale;

public class Country {
    public static final String HYPHEN = "–";
    public static final String PIPE = "|";

    /**
     * 国际长途区号
     *
     * 比如中国区号为'86'
     *
     * 对于'1(264)'之类的会格式化为'1–264'，在 libphonenumber 中会被判定为区号'1'，实际拨号是不包含'–'
     *
     * 个别国家有多个区号，用{@link Country#PIPE}分隔，如'1–787|1–939'
     *
     * 不同国家的区号有可能是相同的，比如美国和加拿大的区号都为'1'
     */
    public String countryCode;
    /**
     * 域名缩写
     *
     * 也称'iso country'、'region'，比如中国为'CN'，所有国家都有唯一的域名缩写
     *
     * 这是在手机设备中能直接获得的信息
     *
     * {@link Locale#getCountry()}、{@link TelephonyManager#getSimCountryIso()}、
     * {@link TelephonyManager#getNetworkCountryIso()}
     */
    public String country; // 域名缩写，比如中国为 “CN”
    /**
     * 国家名称
     *
     * 中国国家为中文名称（e.g. 中国），其他国家为英文名称（e.g. China）
     *
     * 英文名称中间可能会包含空格
     */
    public String name;
    /**
     * 手机号长度
     *
     * 个别国家可能有两种或者三种长度，多个值间用'|'分隔
     */
    public String phoneLength;
    /**
     * 分组
     *
     * 包括常用（'*'）及普通的字母分组（'A–Z'），分组内容不重叠
     *
     * 对于中国地区，以拼音首字母分组，其他情况下以英文首字母分组
     */
    public String group;

    public String[] getCountryCodes() {
        if (countryCode == null) return null;

        String[] countryCodes;
        if (countryCode.contains(PIPE)) {
            countryCodes = countryCode.split(PIPE);
        } else {
            countryCodes = new String[] { countryCode };
        }
        return countryCodes;
    }

    public int[] getCountryCallingCodes() {
        if (countryCode == null) return null;

        String[] countryCodes = getCountryCodes();

        int[] callingCodes = new int[countryCodes.length];
        for (int i = 0; i < countryCodes.length; i++) {
            callingCodes[i] = getSingleCountryCallingCode(countryCodes[i]);
        }
        return callingCodes;
    }

    public static int getSingleCountryCallingCode(String countryCode) {
        try {
            return Integer.valueOf(countryCode.replace(Country.HYPHEN, ""));
        } catch (Exception e) {
            return -1;
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s %s %s %s", name, country, countryCode, phoneLength, group);
    }
}

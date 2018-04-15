/*
 * Tigatrapp
 * Copyright (C) 2013, 2014  John R.B. Palmer, Aitana Oltra, Joan Garriga, and Frederic Bartumeus
 * Contact: info@atrapaeltigre.com
 *
 * This file is part of Tigatrapp.
 *
 * Tigatrapp is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at
 * your option) any later version.
 *
 * Tigatrapp is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Tigatrapp.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file incorporates code from Space Mapper, which is subject
 * to the following terms:
 *
 * 		Space Mapper
 * 		Copyright (C) 2012, 2013 John R.B. Palmer
 * 		Contact: jrpalmer@princeton.edu
 *
 * 		Space Mapper is free software: you can redistribute it and/or modify
 * 		it under the terms of the GNU General Public License as published by
 * 		the Free Software Foundation, either version 3 of the License, or (at
 * 		your option) any later version.
 *
 * 		Space Mapper is distributed in the hope that it will be useful, but
 * 		WITHOUT ANY WARRANTY; without even the implied warranty of
 * 		MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * 		See the GNU General Public License for more details.
 *
 * 		You should have received a copy of the GNU General Public License along
 * 		with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 *
 * This file also incorporates code written by Chang Y. Chung and Necati E. Ozgencil
 * for the Human Mobility Project, which is subject to the following terms:
 *
 * 		Copyright (C) 2010, 2011 Human Mobility Project
 *
 *		Permission is hereby granted, free of charge, to any person obtaining
 *		a copy of this software and associated documentation files (the
 *		"Software"), to deal in the Software without restriction, including
 *		without limitation the rights to use, copy, modify, merge, publish,
 *		distribute, sublicense, and/or sell copies of the Software, and to
 *		permit persons to whom the Software is furnished to do so, subject to
 *		the following conditions:
 *
 *		The above copyright notice and this permission notice shall be included
 *		in all copies or substantial portions of the Software.
 *
 *		THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *		EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 *		MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 *		IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 *		CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 *		TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 *		SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE. *
 */

package ceab.movelab.tigabib.utils;

import android.content.Context;
import android.text.TextUtils;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import ceab.movelab.tigabib.PropertyHolder;
import ceab.movelab.tigabib.R;
import ceab.movelab.tigabib.Util;

import static ceab.movelab.tigabib.UtilLocal.PYBOSSA_TOKEN;
import static ceab.movelab.tigabib.UtilLocal.PYBOSSA_TOKEN_DEV;

/**
 * Created by eideam on 24/06/2017.
 */

public class UtilPybossa {

    private boolean isProduction = false;

    public UtilPybossa(boolean isProduction) {
        this.isProduction = isProduction;
    }

    private void setProductionEnvironment(boolean production) {
        isProduction = production;
    }

    private String getProjectShortName() {
        return (isProduction ? "mosquito-alert" : "mosquito_alert_test");
    }

    public String getUrlToken() {
        return "http://mosquitoalert.pybossa.com/api/auth/project/" + getProjectShortName() + "/token";
    }

    public String getTokenAuth() {
        return (isProduction ? PYBOSSA_TOKEN : PYBOSSA_TOKEN_DEV);
    }

    public void fetchPybossaToken(final Context ctx) {
        String tokenUrl = getUrlToken();
        String authToken = getTokenAuth();

        if ( !PropertyHolder.isInit() )
            PropertyHolder.init(ctx);

        if ( Util.isOnline(ctx) ) {
            Ion.with(ctx)
                .load(tokenUrl)
                .setHeader("Authorization", authToken)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        // do stuff with the result or error
                        if (e != null)
                            Toast.makeText(ctx, ctx.getResources().getString(R.string.pybossa_error) + " > " + e.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        else {
                            if (TextUtils.isEmpty(result)) {
                                Toast.makeText(ctx, R.string.pybossa_error, Toast.LENGTH_SHORT).show();
                            } else {
Util.logInfo("==========++", result);
                                PropertyHolder.setPybossaToken(result);
                            }
                        }
                    }
                });
        }
    }

    public String getPybossaNewtaskUrl(int offset) {
        String projectId = (isProduction ? "1" : "2"); // 1 - production, 2- development
        return UtilPybossa.URL_NEW_TASK + projectId + "/newtask?offset=" + offset + "&external_uid=" + PropertyHolder.getUserId();
    }

    public String getPybossaTaskrunUrl() {
        return UtilPybossa.URL_TASKRUN + "?external_uid=" + PropertyHolder.getUserId();
    }


    /**
     * API new task endpoint.
     */
    public static final String URL_NEW_TASK = "http://mosquitoalert.pybossa.com/api/project/";

    /**
     * API taskrun endpoint.
     */
    public static final String URL_TASKRUN = "http://mosquitoalert.pybossa.com/api/taskrun";

    /**
     * API get photo endpoint.
     */
    public static final String URL_GET_PHOTO = "http://webserver.mosquitoalert.com/get_photo/";



    // Webview URL - not used anymore
    public static final String PYBOSSA_URL = "http://mosquitoalert.pybossa.com/project/mosquito-alert/newtask";

}

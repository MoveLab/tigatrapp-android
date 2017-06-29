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



package ceab.movelab.tigabib;

public class UtilLocal {


	public static final String URL_PROJECT = "http://mosquitoalert.com/";	// blog url

	// Desenvolupament vs produccio !!!!
	// Producció = "http://webserver.mosquitoalert.com/";
	// Desenvolupament: http://humboldt.ceab.csic.es/
	public static final String URL_TIGASERVER =
			(Util.debugMode() ? "http://humboldt.ceab.csic.es/" : "http://webserver.mosquitoalert.com/");

	/**
	 * Server API URL.
	 */
	public static final String URL_TIGASERVER_API_ROOT = URL_TIGASERVER + "api/";
	/**
	 * API user endpoint.
	 */
	public static final String API_USER = "users/";

	/**
	 * API report endpoint.
	 */
	public static final String API_REPORT = "reports/";

	/**
	 * API photo endpoint.
	 */
	public static final String API_PHOTO = "photos/";

	/**
	 * API fix endpoint.
	 */
	public static final String API_FIXES = "fixes/";

	/**
	 * API mission endpoint.
	 */
	public static final String API_MISSION = "missions/";

	/**
	 * API notification endpoint.
	 */
	public static final String API_NOTIFICATION = "user_notifications/";

	/**
	 * API score endpoint.
	 */
	public static final String API_SCORE = "user_score/";

    /**
	 * API nearby reports endpoint.
	 */
	public static final String API_NEARBY_REPORTS = "nearby_reports/";

	/**
	 * API configuration endpoint.
	 */
	public static final String API_CONFIGURATION = "configuration/";

	/**
	 * API token endpoint.
	 */
	public static final String API_FCM_TOKEN = "token/";


	/**
	 * Server authorization.
	 */
	public final static String TIGASERVER_API_KEY = "69P76S700C48256gQue4Z6KK92MC9gM7l8b";
	//public final static String TIGASERVER_CLIENT_ID = "tigatrapp_android_client";
	public final static String TIGASERVER_AUTHORIZATION = "Token " + TIGASERVER_API_KEY;
	
}

/**

SpagoBI - The Business Intelligence Free Platform

Copyright (C) 2005-2009 Engineering Ingegneria Informatica S.p.A.

This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA

**/
package it.eng.spagobi.engines.qbe.analysisstateloaders.worksheet;

import it.eng.spagobi.utilities.engines.SpagoBIEngineRuntimeException;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

public class Version0WorksheetStateLoader extends AbstractWorksheetStateLoader {

	public final static String FROM_VERSION = "0";
    public final static String TO_VERSION = "1";
    
	/** Logger component. */
    private static transient Logger logger = Logger.getLogger(Version0WorksheetStateLoader.class);
	
    public Version0WorksheetStateLoader() {
    	super();
    }
    
    public Version0WorksheetStateLoader(IWorksheetStateLoader loader) {
    	super(loader);
    }
    
	@Override
	public JSONObject convert(JSONObject data) {
		logger.debug("IN");
		
		try {
			logger.debug( "Converting from encoding version [" + FROM_VERSION + "] to encoding version [" + TO_VERSION + "] ..." );
			
			JSONArray sheets = data.optJSONArray("sheets");
			if (sheets != null && sheets.length() > 0) {
				for (int i = 0; i < sheets.length(); i++) {
					JSONObject aSheet = sheets.getJSONObject(i);
					JSONObject content = aSheet.getJSONObject("content");
					String designer = content.getString("designer");
					if (designer.equals("Pivot Table")) {
						JSONObject crosstabDefinition = content.getJSONObject("crosstabDefinition");
						JSONArray rows = crosstabDefinition.getJSONArray("rows");
						convert(rows);
						JSONArray columns = crosstabDefinition.getJSONArray("columns");
						convert(columns);
					}
				}
			}

			logger.debug( "Conversion from encoding version [" + FROM_VERSION + "] to encoding version [" + TO_VERSION + "] terminated succesfully" );
			
		} catch(Throwable t) {
			throw new SpagoBIEngineRuntimeException("Impossible to load from rowData [" + data + "]", t);
		} finally {
			logger.debug("OUT");
		}
		
		return data;
	}

	/**
	 * Puts an empty array for rows/columns attributes' values
	 * @param attributes the attributes on rows/columns
	 * @throws Exception
	 */
	private void convert(JSONArray attributes) throws Exception {
		if (attributes != null && attributes.length() > 0) {
			for (int i = 0; i < attributes.length(); i++) {
				JSONObject anAttribute = attributes.getJSONObject(i);
				JSONArray values = new JSONArray();
				anAttribute.put("values", values.toString());
			}
		}
	}

}

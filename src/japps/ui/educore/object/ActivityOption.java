/*
 * Copyright (C) 2018 Williams Lopez - JApps
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package japps.ui.educore.object;

import japps.ui.util.Log;
import java.awt.Font;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * An activity option generally is composed by a text, a sound, an image or a video
 * This object contains a regular activity option, every activity knows how to use every
 * activity option
 *
 * @author Williams Lopez - JApps
 */
public class ActivityOption{
    
    
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected ActivityOption parent;
    
    protected Map properties;
    protected String id;
    
    
    
    protected ActivityOption(String id, Map<String,String> properties){
        this.id = id;
        this.properties = properties;
    }
    
    private ActivityOption(){
        
    }

    /**
     * Get the id of this activity option
     * @return 
     */
    public String getId() {
        return id;
    }
    

    
    /**
     * Get a key in this activity option
     * @param key
     * @return 
     */
    public String get(Object key) {
        String v = (String)properties.get(id+"."+key);
        if(v!=null && v.equals("null")){
            return null;
        }
        return v;
    }

    /**
     * Set a property in this activity option
     * @param key
     * @param value 
     */
    public void set(String key, Object value) {
        

        String svalue = value==null?"null":value.toString();
        
        if(value instanceof Date){
            svalue = dateFormat.format(value);
        }
        
        if(value instanceof Calendar){
            Calendar c = (Calendar) value;
            svalue = dateFormat.format(c.getTime());
        }
        
        if(value instanceof Path){
            svalue = value.toString();
            svalue = svalue.replaceAll(File.separator, "/");
        }
        
        if(value instanceof Font){
            Font f = (Font)value;
            String family = f.getFamily();
            String style  = "PLAIN";
            int size   = f.getSize();
            switch(f.getStyle()){
                case Font.BOLD: style = "BOLD"; break;
                case Font.ITALIC: style = "ITALIC"; break;
                case (Font.BOLD+Font.ITALIC): style = "BOLDITALIC"; break;
                    
            }
            svalue = family+"-"+style+"-"+size;
        }

        properties.put(id+"."+key, svalue);
    }
    
    /**
     * Get a property value and parse to boolean
     * @param propertyName
     * @return 
     */
    public boolean getBool(String propertyName){
        String v = get(propertyName);
        if(v!=null){
            return Boolean.parseBoolean(v.toLowerCase());
        }
        return false;
    }
    
    /**
     * Get a property value and parse to int
     * -1 if cant find property
     * @param propertyName
     * @return 
     */
    public int getInt(String propertyName){
        String v = get(propertyName);
        if(v!=null){
            return Integer.parseInt(v);
        }
        return -1;
    }
    
    /**
     * Get a property value and parse to double
     * -1 if property does not exist
     * @param propertyName
     * @return 
     */
    public double getDouble(String propertyName){
        String v = get(propertyName);
        if(v!=null){
            return Double.parseDouble(v);
        }
        return -1;
    }
    
    /**
     * Get a property with date format
     * @param propertyName
     * @return
     * @throws ParseException 
     */
    public Date getDate(String propertyName) throws ParseException{
        String v = get(propertyName);
        if(v!=null){
            return dateFormat.parse(v);
        }
        return null;
    }
    
    /**
     * Decode a font into the string value in format
     * Family-STYLE-size
     * @param propertyName
     * @return 
     */
    public Font getFont(String propertyName){
        String v = get(propertyName);
        if(v!=null){
            return Font.decode(v);
        }
        return null;
    }
    
    /**
     * Get a property with path format
     * It replaces all / for Files.separator
     * @param key
     * @return 
     */
    public Path getPath(String key){
        String v = get(key);
        if(v==null) return null;
        v = v.replaceAll("[/]", File.separator);
        return Paths.get(v);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.id);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if(obj instanceof String){
            return id.equals((String)obj);
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ActivityOption other = (ActivityOption) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

    /**
     * Get the parent of an ActivityOption
     * For example
     * A parent of an ActivityOption is an Activity
     * A parent of an Activity is a Learning
     * @return 
     */
    public ActivityOption getParent() {
        return parent;
    }

    
    
    
    
    
    
}

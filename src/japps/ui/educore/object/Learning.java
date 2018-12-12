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

import japps.ui.educore.object.Activity;
import japps.ui.educore.object.ActivityOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Williams Lopez - JApps
 */
public class Learning extends ActivityOption{
    
    private int countIdentityActivity=0;
    List<Activity> activities;
    
    public Learning(String id,Map properties){
        super(id,properties);
    }
    
    
     /**
     * Get the options
     * @return 
     */
    public List<Activity> getActivities(){
        
        if(activities == null){
            activities = new ArrayList<Activity>(){

                @Override
                public boolean add(Activity e) {
                    boolean b = super.add(e);
                    Learning.this.set("activities", getStringActivity());
                    return b;
                }

                @Override
                public void add(int index, Activity element) {
                    super.add(index,element);
                    Learning.this.set("activities", getStringActivity());
                }

                @Override
                public boolean remove(Object o) {
                    boolean b = super.remove(o);
                    Learning.this.set("activities", getStringActivity());
                    return b;
                }

                @Override
                public Activity remove(int index) {
                    Activity o = super.remove(index);
                    Learning.this.set("activities", getStringActivity());
                    return o;
                }

                @Override
                public boolean removeAll(Collection<?> c) {
                    boolean b = super.removeAll(c);
                    Learning.this.set("activities", getStringActivity());
                    return b;
                }

                @Override
                public void clear() {
                    super.clear(); 
                    Learning.this.set("activities", getStringActivity());
                }
            };
            
            String optionString = get("activities");
            
            if(optionString != null && !optionString.isEmpty()){
                String optionIds[] = optionString.split("[,]");
                for(String optionId : optionIds){
                    Activity option = new Activity(optionId.trim(), properties);
                    activities.add(option);
                }
            }
            
        }
        return activities;
    }
    
    /**
     * Adds a new activity option and return it
     * add a standar id to the returned option
     * @return 
     */
    public Activity addNewActivity(){
        this.countIdentityActivity++;
        Activity o = new Activity(id+"."+countIdentityActivity, properties);
        getActivities().add(o);
        o.parent = this;
        return o;
    }
    
    /**
     * Get an string representantion of all option ids
     * @return 
     */
    private String getStringActivity(){
        String str = "";
        for(ActivityOption o : getActivities()){
            str = str + o.id + ",";
        }
        
        if(str.length()>0){
            str = str.substring(0, str.length()-1);
        }
        
        return str;
        
    }
    
    /**
     * Find the option by id, returns null if it cant be found
     * @param id
     * @return 
     */
    public ActivityOption getActivitynById(String id){
        List<Activity> l = getActivities();
        for(Activity o : l){
            if(o.id.equals(id)){
                return o;
            }
        }
        return null;
    }
    
}

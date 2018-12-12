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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * An object to abstract generic properties for an Activity
 * @author Williams Lopez - JApps
 */
public class Activity extends ActivityOption {
    
    public transient static final int NONSTARTED=0;
    public transient static final int STARTED=1;
    public transient static final int COMPLETED=2;
    
    

    private int optionIdentity = 0;
    private int state = 0;
    private List<ActivityOption> options=null;

    
    private transient List<ActivityListener> activityListener = new ArrayList<>();
    

    /**
     * Creates a new Activity
     * @param id The name of this activity, use [0-9a-zA-Z_-]
     * @param properties 
     */
    public Activity(String id, Map properties) {
        super(id,properties);
    }
    
    
    /**
     * Add a new activity listener
     * @param activityListener 
     */
    public void addActivityListener(ActivityListener activityListener){
        this.activityListener.add(activityListener);
    }
    
    /**
     * Remove an activity listener
     * @param activityListener 
     */
    public void removeActivityListener(ActivityListener activityListener){
        this.activityListener.remove(activityListener);
    }
    
    /**
     * Fires all activity listeners
     */
    public void fireActivityListener(){
        this.activityListener.forEach((a)->{ a.state(getState()); });
    }
    
    /**
     * Get the activity listeners
     * @return 
     */
    public List<ActivityListener> getActivityListeners(){
        return this.activityListener;
    }
    

    /**
     * The state of this activity
     * NONSTARTED, STARTED, COMPLETED
     * @return 
     */
    public int getState() {
        return state;
    }

    /**
     * The state of this activity
     * NONSTARTED, STARTED, COMPLETED
     * @param state 
     */
    public void setState(int state) {
        int oldstate = this.state;
        this.state = state;
        if(oldstate != state){
            fireActivityListener();
        }   
    }
    
    /**
     * Get the options
     * @return 
     */
    public List<ActivityOption> getOptions(){
        
        if(options == null){
            options = new ArrayList<ActivityOption>(){

                @Override
                public boolean add(ActivityOption e) {
                    boolean b = super.add(e);
                    Activity.this.set("options", getStringOption());
                    return b;
                }

                @Override
                public void add(int index, ActivityOption element) {
                    super.add(index,element);
                    Activity.this.set("options", getStringOption());
                }

                @Override
                public boolean remove(Object o) {
                    boolean b = super.remove(o);
                    Activity.this.set("options", getStringOption());
                    return b;
                }

                @Override
                public ActivityOption remove(int index) {
                    ActivityOption o = super.remove(index);
                    Activity.this.set("options", getStringOption());
                    return o;
                }

                @Override
                public boolean removeAll(Collection<?> c) {
                    boolean b = super.removeAll(c);
                    Activity.this.set("options", getStringOption());
                    return b;
                }

                @Override
                public void clear() {
                    super.clear(); 
                    Activity.this.set("options", getStringOption());
                }
            };
            
            String optionString = get("options");
            
            if(optionString != null && !optionString.isEmpty()){
                String optionIds[] = optionString.split("[,]");
                for(String optionId : optionIds){
                    ActivityOption option = new ActivityOption(optionId.trim(), this.properties);
                    options.add(option);
                }
            }
            
        }
        return options;
    }
    
    /**
     * Adds a new activity option and return it
     * add a standar id to the returned option
     * @return 
     */
    public ActivityOption addNewOption(){
        optionIdentity++;
        ActivityOption o = new ActivityOption(id+"."+optionIdentity, properties);
        o.parent = this;
        getOptions().add(o);
        return o;
    }
    
    /**
     * Get an string representantion of all option ids
     * @return 
     */
    private String getStringOption(){
        String str = "";
        for(ActivityOption o : getOptions()){
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
    public ActivityOption getOptionById(String id){
        List<ActivityOption> l = getOptions();
        for(ActivityOption o : l){
            if(o.id.equals(id)){
                return o;
            }
        }
        return null;
    }

}

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
package japps.ui.educore;

import japps.ui.component.AccordionPanel;
import japps.ui.component.Button;
import japps.ui.component.Dialogs;
import japps.ui.component.Label;
import japps.ui.component.Panel;
import japps.ui.component.TextField;
import japps.ui.component.ToggleButton;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.swing.JDialog;

/**
 * A component for properties editing
 * @author Williams Lopez - JApps
 */
public class EducoreConfig extends Panel{
    
    
    AccordionPanel configPanel;
    Button btnSave;
    Button btnCancel;
    private static HashMap<String,List<Property>> groups;
    private static JDialog dialog;
    private Properties changed;
    

    public EducoreConfig() {
        configPanel = new AccordionPanel();
        btnSave = new Button(Resources.$("Save"),(e)->{ saveFile(); dialog.setVisible(false); dialog.dispose();});
        btnCancel = new Button(Resources.$("Cancel"),(e)->{ dialog.setVisible(false); dialog.dispose();});
        
        btnSave.setHorizontalTextPosition(Button.CENTER);
        btnCancel.setHorizontalTextPosition(Button.CENTER);
        btnSave.setHorizontalAlignment(Button.CENTER);
        btnCancel.setHorizontalAlignment(Button.CENTER);
        
        createComponents();
        
        setComponents(new Component[][]{
            {configPanel, configPanel},
            {btnSave,btnCancel}
        }, new String[]{Panel.FILL+","+Panel.GROW+","+Panel.RIGHT,Panel.FILL+","+Panel.GROW+","+Panel.LEFT}
         , new String[]{Panel.FILL_GROW_CENTER,"35:35:35,"+Panel.FILL});
    }
    
    public static void launch() {
        EducoreConfig config =new EducoreConfig();
        dialog = Dialogs.create(config, Resources.$("Educore configuration"), true, null);
        dialog.setSize(500, 500);
        dialog.setVisible(true);
    }
    
    private void saveFile(){
        try {
            if(groups == null) return;
            Properties props = new Properties();
            groups.forEach((k,v)->{
                for(Property p: v){
                    if(p.getNewValue() != null ){
                        p.setValue(p.getNewValue());
                    }
                    props.put(p.id, p.getValue());
                }
            });
            Path file = Resources.getUserAppPath().resolve("res").resolve("config").resolve("educore-editable.properties");
            props.store(Files.newOutputStream(file), "Edited by japps.ui.educore.EducoreConfig");
            Log.debug("Properties saved");
        } catch (Exception e) {
            Log.debug("Error guardando la configuracion", e);
        }
    }
    
    private HashMap<String,List<Property>> createPropertiesGroups(){
        String propertiesString = Resources.p("app.tool.configurable.list");
        
        Log.debug("Creating propertis for list: "+propertiesString);
        
        if(propertiesString == null) return null;
        
        String[] propertiesArray = propertiesString.split("[;]");
        
        if(propertiesArray == null && propertiesArray.length == 0) return null;
        
        HashMap<String,List<Property>> props =new HashMap<>();
        
        for(String id : propertiesArray){
            Property p = new Property(id, Resources.getConfigProperties());
            List<Property> group = props.get(p.getGroup());
            if(group == null){
                group = new ArrayList<>();
                props.put(p.getGroup(), group);
            }
            group.add(p);   
        }
        
        return props;
    }
    
    private void createComponents(){
        
        
        groups = createPropertiesGroups();
        
        groups.forEach((k,v)->{
            try {
                Log.debug("Creating group: "+k);
                Component[][] comps =new Component[v.size()][2];
                String[] constraints = new String[v.size()];
                int row = 0;
                for(Property p : v){
                    try {
                        Log.debug("Creating component for property:"+p);
                        createComponent(row, p, comps, constraints);
                    } catch (Exception e) {
                        Log.debug("Error creating component", e);
                    }
                    row++;
                }
                
                configPanel.addGroup(k, comps,new String[]{Panel.FILL,Panel.FILL_GROW_CENTER},constraints);
            } catch (Exception e) {
                Log.debug("Error loading config properties group", e);
            }
        });
        
    }
    
    private void createComponent(int row,Property p,Component[][] comps, String[] constraints){
        
        if(p.getType().equals("BOOLEAN")){
            ToggleButton button = new ToggleButton();
            button.setText(p.getLabel());
            button.setToolTipText(p.getTooltip());
            button.setSelected(Boolean.parseBoolean(p.getValue()));
            button.addActionListener((e)->{
                p.setNewValue(Boolean.toString(button.isSelected()));
            });
            comps[row][0] = button;
            comps[row][1] = button;
            constraints[row] = "35:35:35,"+Panel.FILL_GROW_CENTER;
            button.setPreferredSize(new Dimension(300, 35));
        }
        if(p.getType().equals("TEXT")){
            Label label = new Label();
            TextField text = new TextField();
            label.setText(p.getLabel());
            label.setToolTipText(p.getTooltip());
            text.setToolTipText(p.getTooltip());
            text.setValue(p.getValue());
            text.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    p.setNewValue(text.getValue());
                }
            });
            comps[row][0] = label;
            comps[row][1] = text;
            constraints[row] = "35:35:35"+","+Panel.TOP+","+Panel.GROW+","+Panel.FILL;
        }

    }
    
    
    class Property{
        
        String id;
        Properties props;
        String newValue = null;


        public Property(String id, Properties props) {
            this.id = id;
            this.props = props;
        }

        public String getType() {
            return this.props.getProperty(id+".type");
        }

        public void setType(String type) {
            this.props.setProperty(id+".type", type);
        }

        public String getLabel() {
            return this.props.getProperty(id+".label");
        }

        public void setLabel(String label) {
            this.props.setProperty(id+".type", label);
        }

        public String getTooltip() {
            return this.props.getProperty(id+".tooltip");
        }

        public void setTooltip(String tooltip) {
            this.props.setProperty(id+".type", tooltip);
        }

        public String getGroup() {
            return this.props.getProperty(id+".group");
        }

        public void setGroup(String group) {
            
        }
        
        public String getValue(){
            return this.props.getProperty(id);
        }
        
        public void setValue(String value){
            this.props.setProperty(id, value);
        }
        
        public void setNewValue(String newValue){
            this.newValue = newValue;
        }
        
        public String getNewValue(){
            return newValue;
        }
        

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + Objects.hashCode(this.id);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Property other = (Property) obj;
            if (!Objects.equals(this.id, other.id)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return id;
        }

        
    }

   
    
    
    
}

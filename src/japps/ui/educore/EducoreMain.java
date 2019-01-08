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

import japps.ui.DesktopApp;
import japps.ui.component.Button;
import japps.ui.component.Panel;
import japps.ui.component.TextField;
import japps.ui.component.ToggleButton;
import japps.ui.educore.object.Const;
import japps.ui.educore.object.Learning;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import java.awt.Component;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.stream.Stream;
import static japps.ui.util.Resources.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;

/**
 *
 * @author Williams Lopez - JApps
 */
public class EducoreMain extends Panel{
    
    
    /**
     * Starts the Educore application
     * 
     * The main mathos of EducoreMain executes
     * 
     * DesktopApp.start
     * 
     * This is a common methos of japps-ui-common and builds and launches 
     * a japps ui application
     * 
     * "reconfigure" -> delete all configuration and sets the defaults
     * 
     * @param args 
     */
    public static void main(String[] args) {
        DesktopApp.init("educore", args);
        EducoreMain main = new EducoreMain();
        DesktopApp.start(main);
    }
    
    private List<Learning> learnings;
    private LearningListPanel learningListPanel;
    private ToggleButton btnOrderBy;
    private Button btnConfig;
    private Button btnHelp;
    private Panel leftPanel;
    
    private Panel topPanel;

    public EducoreMain() {
        learningListPanel = new LearningListPanel();
        
        createLeftPanel();
        createTopPanel();
        
        setComponents(new Component[][]{{leftPanel,null,topPanel},{leftPanel,learningListPanel,learningListPanel}},
                new String[]{"60:60:60,"+Panel.FILL_GROW_CENTER,Panel.FILL_GROW_CENTER,Panel.FILL_GROW_CENTER},
                new String[]{"40:40:40,"+Panel.FILL_GROW_CENTER,Panel.FILL_GROW_CENTER});
        try{
            List<Learning> learnings = findLearnings();
            learningListPanel.setLearnings(learnings);
        }catch(Throwable err){
            Log.debug("Error al cargar los learnings",err);
        }
    }
    
    
    /**
     * Creates the top panel
     */
    private void createTopPanel(){
        topPanel = new Panel();
        TextField txSearch = new TextField();
        txSearch.setToolTipText(Resources.$("Search Learnings"));
        txSearch.setMultiline(false);
        txSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                learningListPanel.setLearnings(filter(txSearch.getValue()));
            }
        });
        topPanel.setComponents(new Component[][]{{null,txSearch}});
    }
    
    /**
     * Creates the left panel
     */
    private void createLeftPanel(){
        leftPanel = new Panel();
        
        //leftPanel.setBorder(new RoundedBorder());
        int size = 35;
        
        learningListPanel.setBorder(BorderFactory.createEmptyBorder());
        
        btnOrderBy = new ToggleButton();
        btnOrderBy.setIcon("apps.png",size,size);
        btnOrderBy.addActionListener((e)->{ learningListPanel.setOrderedByTopic(btnOrderBy.isSelected());});
        btnOrderBy.setSelected(learningListPanel.isOrderedByTopic());
        btnOrderBy.setToolTipText($("Order by topic/List all"));
        btnOrderBy.setBorder(BorderFactory.createEmptyBorder());
        
        
        btnConfig = new Button();
        btnConfig.setIcon("settings.png", size, size);
        btnConfig.addActionListener((e)->{ EducoreConfig.launch(); });
        btnConfig.setToolTipText($("Change settings"));
        btnConfig.setBorder(BorderFactory.createEmptyBorder());
        
        btnHelp = new Button();
        btnHelp.setIcon("help.png", size, size);
        btnHelp.addActionListener(null);
        btnHelp.setToolTipText($("Open help"));
        btnHelp.setBorder(BorderFactory.createEmptyBorder());
        
        leftPanel.setComponents(new Component[][]{
            {btnOrderBy},
            {btnConfig},
            {btnHelp}
        }, new String[]{Panel.FILL_GROW_CENTER},
        new String[]{
            "60:60:60",
            "60:60:60",
            "60:60:60"
        });
    }
    
    /**
     * Find all learnings configured in $user/japps/educore/learnings
     * @return
     * @throws IOException 
     */
    private List<Learning> findLearnings() throws IOException{
        Path dir = Resources.getUserAppPath().resolve("learnings");
        Stream<Path> s =Files.list(dir);
        List<Learning> learnings = new ArrayList<>();
        s.forEach((p)->{
            
            try {
                
                if(!Files.isDirectory(p)){
                    return;
                }
                
                Path propPath = p.resolve("learning.properties");
                
                if(!Files.exists(propPath)){
                    return;
                }
                
                Properties properties = new Properties();
                properties.load(Files.newInputStream(propPath));
                Learning learning = new Learning(p.getFileName().toString(), properties);
                
                
                double version1 = Const.LEARNING.getEducoreVersion(learning);
                double version2 = Double.parseDouble(Resources.p("app.version"));
                
                if(version1 <= version2){
                    learnings.add(learning);
                }
                
            } catch (Exception e) {
                Log.debug("Error loading Learning on : "+p,e);
            }
            
            
        });
        
        return learnings;
        
    }
    
    /**
     * Filter the learnings
     * @param filter 
     */
    public List<Learning> filter(String filter){
        
        List<Learning> filtered = new ArrayList<>();
        
        try {
            if (learnings == null) {
                learnings = findLearnings();
            }
            
            learnings.forEach((l)->{ 
                String t = Const.LEARNING.getTitle(l);
                if(t!=null && t.toLowerCase().contains(filter.toLowerCase())){
                    filtered.add(l);
                }
            });
            
        } catch (Exception err) {
            Log.debug("Error al filtrar los learnings");
        }
        
        return filtered;
        
    }
    
    
    
    
}

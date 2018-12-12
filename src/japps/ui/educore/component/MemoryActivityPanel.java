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
package japps.ui.educore.component;

import japps.ui.DesktopApp;
import japps.ui.component.Button;
import japps.ui.component.ToggleButton;
import java.awt.Component;
import japps.ui.educore.object.Activity;
import japps.ui.educore.object.ActivityOption;
import static japps.ui.educore.object.Const.MEMORY.*;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import japps.ui.util.Util;
import java.awt.Color;
import java.awt.Image;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;

/**
 * This a memory like activity panel
 * @author Williams Lopez - JApps
 */
public class MemoryActivityPanel extends ActivityPanel {
    
    private Card lastSelected = null;
    
    private int cardWidth;
    private int cardHeight;
    private int rows;
    private int columns;
    private Image backImage;
    private Card[] cards;

    /**
     * Constructs the memory panel
     */
    public MemoryActivityPanel() {

    }

    /**
     * Choose an option cardId
     * @param cardId 
     */
    private void chooseOption(int cardId){
        try {
            
            Card card = cards[cardId];
            
            if(lastSelected == null){
                card.front();
                lastSelected = card;
                
                if (isSpeechText(card.option)) {
                        Resources.speech(getText(card.option));
                 }
                
            }else{
                
                if(lastSelected == card){
                    return;
                }
                
                if(lastSelected.compareCommands(card)){
                    card.front();
                    lastSelected.complete();
                    card.complete();
                    lastSelected = null;
                    
                    if(countPending()<=0){
                        getActivity().setState(Activity.COMPLETED);
                    }
                    
                    
                }else{
                    card.front();
                    setEnableCards(false);
                    javax.swing.Timer timer = new javax.swing.Timer(1000, (e)->{
                        lastSelected.back();
                        card.back();
                        lastSelected = null;
                        setEnableCards(true);
                    });
                    timer.setRepeats(false);
                    timer.start();
                }
            }
            
        } catch (Exception e) {
            Log.debug("Error chooseOption - MemoryActivityPanel", e);
        }
        
    }
    
    /**
     * Enable/disable all cards
     * @param bool 
     */
    private void setEnableCards(boolean bool){
        for(Card card  : cards){
            card.setEnabled(bool);
        }
    }
    
    /**
     * Restarts all cards
     */
    public void reset(){
        for(Card c : cards){
            c.reset();
        }
    }
    
    /**
     * Count all pending cards
     * @return 
     */
    public int countPending(){
        int count = 0;
        for(Card c  : cards){
            count += c.completed ? 0 : 1;
        }
        return count;
    }
    
    /**
     * Internal method to duplicate and mix the cards
     * @param a
     * @return 
     */
    private ActivityOption[] duplicateAndMix(Activity a){
        int size = a.getOptions().size()*2;
        ActivityOption[] duplicatedAndMixed = new ActivityOption[size];
        List<Integer> used = new ArrayList<>();
        for(ActivityOption o : a.getOptions()){
            int pos = 0;
            do{
                pos = (int)(Math.random()*((double)size));
            }while(used.contains(pos));
            used.add(pos);
            duplicatedAndMixed[pos] = o;
        }
        for(ActivityOption o : a.getOptions()){
            int pos = 0;
            do{
                pos = (int)(Math.random()*((double)size));
            }while(used.contains(pos));
            used.add(pos);
            duplicatedAndMixed[pos] = o;
        }
        
        return duplicatedAndMixed;
    }


    @Override
    protected void build(Activity a) {
        

        if (a == null || a.getOptions() == null || a.getOptions().isEmpty()) {
            return;
        }
        
        rows = getRows(a);
        columns = getColumns(a);
        cardHeight = getThumbnailHeight(a);
        cardWidth = getThumbnailWidth(a);
        backImage = Util.readImage(getThumbnailBack(a)).getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH);
        
        if (rows * columns < (getActivity().getOptions().size()*2)) {
            throw new RuntimeException("Cant place all " + getActivity().getOptions().size() + " options in matrix: [rows:" + rows + ",cols:" + columns + "]");
        }

        Component[][] comp = new Component[rows][columns];
        ActivityOption[] mixed = duplicateAndMix(a);
        cards = new Card[mixed.length];

        int r = 0, c = 0;

        int i=0;
        for (ActivityOption option : mixed) {

            Card card = new Card(i);
            card.button = new ToggleButton();
            card.completed = false;
            card.option = option;
            cards[i] = card;
            
            try{
                card.frontImage = Util.readImage(getThumbnail(option)).getScaledInstance(cardWidth, cardHeight, Image.SCALE_SMOOTH);
                card.button.setCommand(option.getId());
                card.button.setBorder(BorderFactory.createEmptyBorder());
                card.button.setAction((e) -> {
                    final int cardId = card.id;
                    String optionid = e.getActionCommand();
                    ActivityOption o = getActivity().getOptionById(optionid);
                    Button b = (Button)e.getSource();
                    chooseOption(cardId);
                });
                card.button.setVerticalAlignment(Button.CENTER);
                card.button.setHorizontalAlignment(Button.CENTER);
                
                if(backImage!=null){
                    card.button.setImage(backImage);
                }else{
                    Log.debug("Cant find image for card:"+getTitle(option));
                }

                comp[r][c] = card.button;
                c++;
                if (c >= columns) {
                    c = 0;
                    r++;
                }
            }catch(Throwable err){
                Log.debug("Error creating card for option: "+option.getId(),err);
            }
            
            i++;
        }
        setComponents(comp);
    }
    

    @Override
    public void release() {
        //does not need release implementation
    }

    
    private class Card{
        int id;
        ActivityOption option;
        ToggleButton button;
        boolean completed;
        Image frontImage;
        
        public Card(int id){
            this.id = id;
        }
        
        public void back(){
            this.button.setImage(backImage);
            this.button.setSelected(false);
        }
        
        public void front(){
            this.button.setImage(frontImage);
        }
        
        public void complete(){
            this.completed = true;
            this.button.setSelected(true);
            this.button.setEnabled(false);
        }
        
        public void setEnabled(boolean enabled){
            if(!this.completed){
                this.button.setEnabled(enabled);
            }
        }
        
        public boolean compareCommands(Card other){
            return this.button.getCommand().equals(other.button.getCommand());
        }
        
        public void reset(){
            this.completed = false;
            this.button.setEnabled(true);
            this.button.setSelected(false);
            back();
        }
        
    }
    

}

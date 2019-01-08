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

import japps.ui.component.Label;
import japps.ui.component.Panel;
import japps.ui.component.TextField;
import japps.ui.educore.object.Activity;
import japps.ui.educore.object.ActivityOption;
import japps.ui.educore.object.Const;
import java.awt.Component;
import static japps.ui.educore.object.Const.INPUTTEXT.*;
import japps.ui.util.Log;
import japps.ui.util.Resources;
import japps.ui.util.Util;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.nio.file.Path;

/**
 *
 * @author Williams Lopez - JApps
 */
public class InputTextActivityPanel extends ActivityPanel{

    
    
    public InputTextActivityPanel() {
        super();
    }

    
    
    
    @Override
    protected void build(Activity activity) {
        
        int size = activity.getOptions().size();
        Component[][] comps= new Component[size][2];
        String[] rows = new String[size];

        int count =0;
        for (ActivityOption o : activity.getOptions()) {
            String text = getText(o);
            boolean speech = isSpeechText(o);
            String inputName = getInputName(o);
            String input = getInput(activity);
            Font font = Const.INPUTTEXT.getFont(o);

            Label label = new Label();
            TextField textField = new TextField();

            label.setText(text);
            label.setFont(font);
            
            textField.setFont(font);
            textField.setValue(input == null ? "" : input);
            textField.addKeyListener(new KeyAdapter() {
                @Override
                public void keyReleased(KeyEvent e) {
                    if (textField.getValue() != null && !textField.getValue().equals("")) {
                        setInput(o, textField.getValue());
                        getActivity().setState(Activity.COMPLETED);
                    } else {
                        getActivity().setState(Activity.STARTED);
                    }
                }
            });
            textField.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if(speech){
                        Resources.speech(text,false);
                    }
                }
                
            });
            comps[count][0]=label;
            comps[count][1]=textField;
            
            int fh = font.getSize()+25;
            rows[count] = fh+":"+fh+":"+fh+","+Panel.FILL+","+Panel.CENTER;
            count++;
        }
        setComponents(comps,
                new String[]{ Panel.NONE, Panel.FILL_GROW_CENTER },
                rows);
    }

    @Override
    public void start() {
        super.start();
    }
    
    

    @Override
    public void release() {
        
    }
    
    
}

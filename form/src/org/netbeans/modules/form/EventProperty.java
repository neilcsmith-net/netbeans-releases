
package org.netbeans.modules.form;

import java.util.*;
import java.beans.*;
import java.awt.event.*;

import org.openide.*;
import org.openide.nodes.*;
import org.openide.explorer.propertysheet.editors.*;
import org.openide.util.Utilities;

/** 
 * Property implementation class for events of metacomponents (RADComponent).
 * (Events are treated as properties on Events tab of Component Inspector.)
 */
public class EventProperty extends PropertySupport.ReadWrite {

    /** Event object holding info about one component's event and
     * event handlers attached to it.
     */
    Event event;

    /** Last selected event handler (from those attached to this event).
     * Name of this handler is displayed in property sheet.
     */
    EventHandler lastSelectedHandler;

    EventProperty(Event event) {
        super(FormEditor.EVENT_PREFIX + event.getName(),
              String.class,
              event.getName(),
              event.getName());

        this.event = event;
        setShortDescription(event.getListenerMethod().getDeclaringClass().getName());
    }

    /** Getter for the value of the property. It returns name of the last
     * selected event handler (for property sheet), not the event itself.
     * @return String name of the selected event handler attached to the event
     */
    public Object getValue() {
        Vector handlers = event.getHandlers();

        if (handlers.size() == 0)
            lastSelectedHandler = null;
        else if (lastSelectedHandler == null || !handlers.contains(lastSelectedHandler))
            lastSelectedHandler = (EventHandler)handlers.get(0);

        return lastSelectedHandler != null ? lastSelectedHandler.getName() : null;
    }

    /** Setter for the value of the property. It accepts String (for adding
     * new or renaming the last selected event handler) or HandlerSetChange
     * object (describing any changes in attached event handlers).
     */
    public void setValue(Object val) throws IllegalArgumentException {
        if (val == null) return;

        HandlerSetChange change = null;
        String newSelectedHandler = null;

        if (val instanceof HandlerSetChange)
            change = (HandlerSetChange) val;
        else {
            if (val instanceof String) {
                change = new HandlerSetChange();

                if (event.getHandlers().size() > 0) {
                    String current = lastSelectedHandler == null ?
                        ((EventHandler)event.getHandlers().get(0)).getName() :
                        lastSelectedHandler.getName();

                    if ("".equals(val)) { // empty String => remove current
                        change.getRemoved().add(current);
                    }
                    else { // valid String => rename current
                        change.getRenamedNewNames().add((String)val);
                        change.getRenamedOldNames().add(current);
                        newSelectedHandler = (String)val;
                    }
                }
                else {
                    change.getAdded().add((String)val);
                    newSelectedHandler = (String)val;
                }
            }
            else throw new IllegalArgumentException();
        }

        FormModel formModel = event.getComponent().getFormModel();
        FormEventHandlers formHandlers = formModel.getFormEventHandlers();

        if (change.hasRemoved()) { // some handlers to remove
            for (Iterator iter = change.getRemoved().iterator(); iter.hasNext();) {
                formHandlers.removeEventHandler(event, (String)iter.next());
                formModel.fireFormChanged();
            }
        }

        if (change.hasRenamed()) { // some handlers to rename
            for (int k=0, n=change.getRenamedOldNames().size(); k < n; k++) {
                String oldName = (String) change.getRenamedOldNames().get(k);
                String newName = (String) change.getRenamedNewNames().get(k);

                if (!Utilities.isJavaIdentifier(newName))
                    continue; // invalid name (checked by EventCustomEditor)
                if (newName.equals(oldName))
                    continue; // no change

                formHandlers.renameEventHandler(oldName, newName);
                formModel.fireFormChanged();
            }
        }

        if (change.hasAdded()) { // some handlers to add
            for (Iterator iter = change.getAdded().iterator(); iter.hasNext();) {
                String handlerName = (String) iter.next();
                if (!Utilities.isJavaIdentifier(handlerName)) { // invalid name
                    TopManager.getDefault().notify(new NotifyDescriptor.Message(java.text.MessageFormat.format(FormEditor.getFormBundle().getString("FMT_MSG_InvalidJavaIdentifier"), new Object [] {handlerName}), NotifyDescriptor.ERROR_MESSAGE));
                    continue;
                }

                if (formHandlers.addEventHandler(event, handlerName))
                    formModel.fireFormChanged();
                else continue; // incompatible handler
            }
        }

        event.getComponent().getNodeReference().firePropertyChangeHelper(
                this.getName(), null, null); //lastSelectedHandler, newSelectedHandler);

        lastSelectedHandler = formHandlers.getEventHandler(newSelectedHandler);
    }

    public boolean canWrite() {
        return !isReadOnly();
    }

    private boolean isReadOnly() {
        return event.getComponent().isReadOnly();
    }

    /** Returns property editor for this property.
     * @return the property editor for adding/removing/renaming event handlers
     */
    public java.beans.PropertyEditor getPropertyEditor() {
        return new EventEditor();
    }

    /** Helper class describing changes in event handlers attached to the event.
     */
    public class HandlerSetChange {
        boolean hasAdded() {
            return(added !=null && added.size()>0);
        }
        boolean hasRemoved() {
            return(removed !=null && removed.size()>0);
        }
        boolean hasRenamed() {
            return(renamedOldName !=null && renamedOldName.size()>0);
        }
        List getAdded() {
            if (added == null) added = new ArrayList();
            return added;
        }
        List getRemoved() {
            if (removed == null) removed = new ArrayList();
            return removed;
        }
        List getRenamedOldNames() {
            if (renamedOldName == null) renamedOldName = new ArrayList();
            return renamedOldName;
        }
        List getRenamedNewNames() {
            if (renamedNewName == null) renamedNewName = new ArrayList();
            return renamedNewName;
        }
        private ArrayList added;
        private ArrayList removed;
        private ArrayList renamedOldName;
        private ArrayList renamedNewName;
    }

    /** Property editor class for EventProperty. It provides in-place editor
     * and custom editor for adding/removing/renaming event handlers.
     */
    class EventEditor extends PropertyEditorSupport implements EnhancedPropertyEditor
    {
        boolean advancedFeatures;

        ActionListener comboSelectListener = null;
        FocusListener comboEditFocusListener = null;

        javax.swing.JComboBox eventCombo;

        EventEditor() {
            FormEditorSupport s = event.getComponent().getFormModel().getFormEditorSupport();
            advancedFeatures = s.supportsAdvancedFeatures();
        }

        public String getAsText() {
            if (this.getValue() == null)
                return FormEditor.getFormBundle().getString("CTL_NoEvent");
            else
                return this.getValue().toString();
        }

        public void setAsText(String selected) {
            this.setValue(selected);
        }

        public boolean supportsEditingTaggedValues() {
            return false;
        }

        /**
         * @return custom property editor to be shown inside
         * the property sheet.
         */
        public java.awt.Component getInPlaceCustomEditor() {
            Vector handlers = event.getHandlers();
            if (advancedFeatures) {
                eventCombo = new javax.swing.JComboBox();
                eventCombo.setEditable(!EventProperty.this.isReadOnly());

                if (handlers.size() == 0) {
                    eventCombo.getEditor().setItem(FormUtils.getDefaultEventName(
                            event.getComponent(), event.getListenerMethod()));
                } else {
                    for (int i=0, n=handlers.size(); i < n; i++) {
                        eventCombo.addItem(((EventHandler) handlers.get(i)).getName()); // [PENDING]
                    }
                    if (lastSelectedHandler != null)
                        eventCombo.setSelectedItem(lastSelectedHandler.getName());
                }

                // listening on combobox selection
                if (comboSelectListener == null)
                    comboSelectListener = new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            if (event.getHandlers().size() > 0) {
                                int i = eventCombo.getSelectedIndex();
                                if (i >= 0) {
                                    lastSelectedHandler = (EventHandler) event.getHandlers().get(i);
                                    String selected = lastSelectedHandler.getName();
                                    EventEditor.this.setValue(selected);
                                    event.gotoEventHandler(selected);
                                } 
                            }
                        }
                    };
                eventCombo.addActionListener(comboSelectListener);

                // listening on combobox focus
                if (!EventProperty.this.isReadOnly()) {
                    eventCombo.addFocusListener(new FocusAdapter() {
                        public void focusGained(FocusEvent evt) {
                            Vector ehandlers = event.getHandlers();
                            eventCombo.removeAllItems();
                            if (ehandlers.size() == 0) {
                                eventCombo.getEditor().setItem(FormUtils.getDefaultEventName(
                                        event.getComponent(), event.getListenerMethod()));
                            } else {
                                for (int i=0, n = ehandlers.size(); i < n; i++) {
                                    eventCombo.addItem(((EventHandler) ehandlers.get(i)).getName());
                                }
                                if (lastSelectedHandler != null)
                                    eventCombo.setSelectedItem(lastSelectedHandler.getName());
                            }
                        }
                    });

                    // listening on combobox's editor focus
                    if (comboEditFocusListener == null)
                        comboEditFocusListener = new FocusAdapter() {
                            public void focusLost(FocusEvent evt) {
                                EventEditor.this.setValue(null);
                            }
                        };
                    eventCombo.getEditor().getEditorComponent().addFocusListener(
                                                       comboEditFocusListener);

                    // listening on combobox's editor action
                    eventCombo.getEditor().addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            eventCombo.removeActionListener(comboSelectListener);
                            eventCombo.getEditor().getEditorComponent()
                                .removeFocusListener(comboEditFocusListener);
                            String selected = (String) eventCombo.getEditor().getItem();
                            EventEditor.this.setValue(selected);
                            if (!"".equals(selected))
                                event.gotoEventHandler(selected);
                        }
                    });
                }

                return eventCombo;
            }
            else {
                final javax.swing.JTextField eventField = new javax.swing.JTextField();
                if (handlers.size() > 0)
                    eventField.setText(((EventHandler) handlers.get(0)).getName());
                if (EventProperty.this.isReadOnly())
                    eventField.setEditable(false);
                else {
                    if (handlers.size() == 0)
                        eventField.setText(FormUtils.getDefaultEventName(
                            event.getComponent(), event.getListenerMethod()));

                    eventField.addActionListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent e) {
                            setAsText(eventField.getText());
                        }
                    });
                }

                return eventField;
            }
        }

        /** @return true if this PropertyEditor provides a enhanced in-place
         * custom property editor, false otherwise
         */
        public boolean hasInPlaceCustomEditor() {
            return !EventProperty.this.isReadOnly()
                     || EventProperty.this.event.getHandlers().size() > 0;
        }

        public boolean supportsCustomEditor() {
            return advancedFeatures;
//                     && !EventProperty.this.isReadOnly();
        }

        /** @return the custom property editor (a standalone panel) for
         * editing event handlers attached to the event.
         */
        public java.awt.Component getCustomEditor() {
            if (EventProperty.this.isReadOnly())
                return null;

            final EventCustomEditor ed = new EventCustomEditor(EventProperty.this);
            DialogDescriptor dd = new DialogDescriptor(ed,
                    java.text.MessageFormat.format(FormEditor.getFormBundle().getString("FMT_MSG_HandlersFor"), new Object [] {event.getName()}),
                    true,
                    new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent evt) {
                            if (evt.getSource().equals(DialogDescriptor.OK_OPTION)) {
                                ed.doChanges();
                            }
                        }
                    });

            return TopManager.getDefault().createDialog(dd);
        }
    }
}

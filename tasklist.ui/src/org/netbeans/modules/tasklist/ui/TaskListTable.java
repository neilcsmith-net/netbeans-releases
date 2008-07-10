/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2007 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.tasklist.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.plaf.TableHeaderUI;
import javax.swing.plaf.TableUI;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.netbeans.modules.tasklist.impl.OpenTaskAction;
import org.netbeans.modules.tasklist.ui.Util;
import org.netbeans.spi.tasklist.Task;
import org.netbeans.modules.tasklist.trampoline.TaskGroup;
import org.openide.util.NbBundle;

/**
 *
 * @author S. Aubrecht
 */
class TaskListTable extends JTable {
    
    private TableCellRenderer foldingGroupRenderer;
    private TableCellRenderer groupRenderer;
    
    private Action defaultAction = new DefaultAction();
    
    private Action nextTaskAction = new TaskNavigationAction( true );
    private Action prevTaskAction = new TaskNavigationAction( false );
    
    /** Creates a new instance of TaskListTable */
    public TaskListTable() {
        addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent e ) {
                maybePopup( e );
            }
            
            @Override
            public void mouseReleased( MouseEvent e ) {
                maybePopup( e );
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = rowAtPoint( e.getPoint() );
                if( isFoldingModel() ) {
                    FoldingTaskListModel foldingModel = getFoldingModel();
                    if( e.getClickCount() == 2 ) {
                        //handle group collapse/expand
                        if( foldingModel.isGroupRow( row ) ) {
                            foldingModel.toggleGroupExpanded( row );
                            return;
                        }
                    } else if( e.getClickCount() == 1 ) {
                        if( foldingModel.isGroupRow( row ) ) {
                            maybeExpandGroup( e.getPoint() );
                            return;
                        }
                    }
                }
                if( e.getClickCount() == 2 ) {
                    defaultAction.actionPerformed( null );
                }
            }
        });
        setSelectionMode( ListSelectionModel.SINGLE_SELECTION );
        setBorder( BorderFactory.createEmptyBorder() );
        
        setAutoCreateColumnsFromModel( false );
        
        int requiredRowHeight = 16+2+2*getIntercellSpacing().height;
        setRowHeight( Math.max( getRowHeight(), requiredRowHeight ) );
        
        InputMap inputMap = getInputMap( JTable.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT );
        Object right = inputMap.get( KeyStroke.getKeyStroke( KeyEvent.VK_RIGHT, 0 ) );
        Object left = inputMap.get( KeyStroke.getKeyStroke( KeyEvent.VK_LEFT, 0 ) );
        inputMap.put( KeyStroke.getKeyStroke( KeyEvent.VK_ENTER, 0 ), "defaultAction" ); // NOI18N
        
        getInputMap( JTable.WHEN_FOCUSED ).put( KeyStroke.getKeyStroke( KeyEvent.VK_F10, KeyEvent.SHIFT_DOWN_MASK ), "org.openide.actions.PopupAction" ); //NOI18N
        getActionMap().put( "org.openide.actions.PopupAction", new PopupAction() ); //NOI18N
        
        ActionMap actionMap = getActionMap();
        Action actionLeft = actionMap.get( left );
        Action actionRight = actionMap.get( right );
        
        actionMap.put( left, new ToggleGroupAction( false, actionLeft ) );
        actionMap.put( right, new ToggleGroupAction( true, actionRight ) );
        actionMap.put( "defaultAction", defaultAction ); // NOI18N
        
        actionMap.put( "jumpPrev", prevTaskAction ); // NOI18N
        actionMap.put( "jumpNext", nextTaskAction ); // NOI18N
//        actionMap.put (FindAction.class.getName (), this.findAction);
//        actionMap.put (javax.swing.text.DefaultEditorKit.copyAction, this.copyAction);
    }
    
    @Override
    public void createDefaultColumnsFromModel() {
        TableModel m = getModel();
        if( m != null ) {
            // Remove any current columns
            TableColumnModel cm = getColumnModel();
            while( cm.getColumnCount() > 0 ) {
                cm.removeColumn( cm.getColumn(0) );
	    }

            // Create new columns from the data model info
            for( int i=0; i<m.getColumnCount(); i++ ) {
                TableColumn newColumn = new MyTableColumn(i);
                if( i == TaskListModel.COL_LOCATION )
                    newColumn.setCellRenderer( new LeftDotRenderer() );
                else if( i != TaskListModel.COL_GROUP )
                    newColumn.setCellRenderer( new TooltipRenderer() );
                addColumn(newColumn);
            }
        }
    }
    
    @Override
    public void removeNotify() {
        if( getModel() instanceof TaskListModel )
            storeColumnState();
        super.removeNotify();
    }
    
    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new MyTableHeader( columnModel );
    }
    
    @Override
    public TableCellRenderer getCellRenderer( int row, int col ) {
        if( col == 0 ) {
            if( isFoldingModel() && getFoldingModel().isGroupRow( row ) )
                return getFoldingGroupRenderer();
            return getGroupRenderer();
        }
        return super.getCellRenderer( row, col );
    }
    
    private void maybePopup( MouseEvent e ) {
        if( e.isPopupTrigger() ) {
            e.consume();
            int row = rowAtPoint( e.getPoint() );
            if( row >= 0 )
                getSelectionModel().setSelectionInterval( row, row );
            JPopupMenu popup = Util.createPopup( this );
            popup.show( this, e.getX(), e.getY() );
        }
    }
    
    private TableCellRenderer getFoldingGroupRenderer() {
        if( null == foldingGroupRenderer )
            foldingGroupRenderer = new FoldingTaskGroupRenderer();
        return foldingGroupRenderer;
    }
    
    private TableCellRenderer getGroupRenderer() {
        if( null == groupRenderer )
            groupRenderer = new TaskGroupRenderer();
        return groupRenderer;
    }
    
    private void maybeExpandGroup( Point point ) {
        int row = rowAtPoint( point );
        int col = columnAtPoint( point );
        if( 0 != col )
            return;
        Rectangle rect = getCellRect( row, col, false );
        TableCellRenderer renderer = getCellRenderer(row, col);
        prepareRenderer(renderer, row, col);
        if( renderer instanceof FoldingTaskGroupRenderer ) {
            Icon icon = ((FoldingTaskGroupRenderer)renderer).getIcon();
            rect.grow( 0, -(rect.height-openedIcon.getIconHeight())/2 );
            rect.x = getColumnModel().getColumnMargin(); //rect.width - icon.getIconWidth();
            rect.width = openedIcon.getIconWidth();
            if( rect.contains( point ) ) {
                FoldingTaskListModel foldingModel = getFoldingModel();
                foldingModel.toggleGroupExpanded( row );
            }
        }
    }
    
    private void storeColumnState() {
        int count = getColumnModel().getColumnCount();
        for( int i=0; i<count; i++ ) {
            TableColumn tc = getColumnModel().getColumn( i );
            if( tc instanceof MyTableColumn ) {
                ((MyTableColumn)tc).storeState();
            }
        }
    }
    
    @Override
    public void setUI( TableUI ui ) {
        super.setUI( new TaskListTableUI() );
        setTableHeader( createDefaultTableHeader() );
    }
    
    @Override
    public void setModel( TableModel newModel ) {
        if( getModel() instanceof TaskListModel && newModel != getModel() ) {
            storeColumnState();
            ((TaskListModel)getModel()).detach();
        }
            
        if( newModel instanceof TaskListModel ) {
            ((TaskListModel)newModel).attach();
        }
        super.setModel( newModel );
        createDefaultColumnsFromModel();
    }

    private boolean isFoldingModel() {
        return getModel() instanceof FoldingTaskListModel;
    }
    
    private FoldingTaskListModel getFoldingModel() {
        return isFoldingModel() ? (FoldingTaskListModel)getModel() : null;
    }
    
    @Override
    public String getToolTipText( MouseEvent e ) {
        int hitRowIndex = rowAtPoint( e.getPoint() );
        if( isFoldingModel() && getFoldingModel().isGroupRow( hitRowIndex ) ) {
            return getFoldingModel().getGroupAtRow( hitRowIndex ).getGroup().getDescription();
        }
        return super.getToolTipText( e );
    }
    
    Task getSelectedTask() {
        int selRow = getSelectedRow();
        if( selRow < 0 )
            return null;
        return ((TaskListModel)getModel()).getTaskAtRow( selRow );
    }
    
    int getSortColumn() {
        return ((TaskListModel)getModel()).getSortingColumnn();
    }
    
    void setSortColumn( int col ) {
        ((TaskListModel)getModel()).toggleSort( col );
    }
    
    boolean isAscendingSort() {
        return ((TaskListModel)getModel()).isAscendingSort();
    }
    
    void setAscendingSort( boolean asc ) {
        ((TaskListModel)getModel()).setAscendingSort( asc );
    }
    
//    @Override
//    public Rectangle getCellRect( int row, int col, boolean includeSpacing ) {
//        Rectangle res = super.getCellRect( row, col, includeSpacing );
//        if( isFoldingModel() && getFoldingModel().isGroupRow(row) ) {
//            res.x = 0;
//            res.width = getWidth();
//            if( !includeSpacing )
//                res.width -= getColumnModel().getColumnMargin();
//        }
//        return res;
//    }
    
    
    
    private static final Icon openedIcon = (Icon)UIManager.get("Tree.expandedIcon"); // NOI18N
    private static final Icon closedIcon = (Icon)UIManager.get("Tree.collapsedIcon"); // NOI18N
    
    private static Map<Image, ImageIcon> iconCache = new HashMap<Image, ImageIcon>(10);
    
    private class TaskGroupRenderer extends DefaultTableCellRenderer {
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                              boolean isSelected, boolean hasFocus, int row, int column) {
            Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if( res instanceof JLabel ) {
                if( value instanceof TaskGroup ) {
                    TaskGroup tg = (TaskGroup)value;
                    JLabel renderer = (JLabel)res;
                    renderer.setText( null );
                    ImageIcon icon = iconCache.get( tg.getIcon() );
                    if( null == icon ) {
                        icon = new ImageIcon( tg.getIcon() );
                        iconCache.put( tg.getIcon(), icon );
                    }
                    renderer.setIcon( icon );
                    renderer.setToolTipText( tg.getDescription() );
                    renderer.setHorizontalAlignment( JLabel.RIGHT );
                }
            }
            return res;
        }
    }

    private class FoldingTaskGroupRenderer extends DefaultTableCellRenderer {
        
        private final Color GTK_BK_COLOR = new Color( 184,207,229 );
        private final Color AQUA_BK_COLOR = new Color(225, 235, 240);

        private final boolean isGTK = "GTK".equals( UIManager.getLookAndFeel().getID() ); // NOI18N
        private final boolean isAqua = "Aqua".equals( UIManager.getLookAndFeel().getID() ); // NOI18N
        
        public FoldingTaskGroupRenderer() {
            //force initialization of PropSheet look'n'feel values 
            UIManager.get( "nb.propertysheet" ); // NOI18N
            setOpaque( true );
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                              boolean isSelected, boolean hasFocus, int row, int column) {
            
            if( getFoldingModel().isGroupRow( row ) ) {
                hasFocus = table.isFocusOwner() && isSelected;
            }
            Component res = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if( res instanceof JLabel ) {
                if( value instanceof FoldingTaskListModel.FoldingGroup ) {
                    FoldingTaskListModel.FoldingGroup fg = (FoldingTaskListModel.FoldingGroup)value;
                    JLabel renderer = (JLabel)res;
                    renderer.setText( fg.getGroup().getDisplayName() + " ("+fg.getTaskCount()+")" );
                    Icon treeIcon = fg.isExpanded() ? openedIcon : closedIcon;
                    renderer.setIcon( treeIcon );
                    renderer.setToolTipText( fg.getGroup().getDescription() );
                    renderer.setHorizontalAlignment( JLabel.LEFT );
                    if( !isSelected )
                        renderer.setBackground( getBackgroundColor() );
                }
            }
            return res;
        }
        
        public Color getBackgroundColor() {
            if( isGTK ) {
                return GTK_BK_COLOR;
            } else if( isAqua ) {
                return AQUA_BK_COLOR;
            } else {
                return UIManager.getColor( "PropSheet.setBackground" ); //NOI18N
            }
        }
    }
    
    
    
    private class ToggleGroupAction extends AbstractAction {
        private Action defaultAction;
        private boolean expand;
        public ToggleGroupAction( boolean expand, Action defaultAction ) {
            this.defaultAction = defaultAction;
            this.expand = expand;
        }
    
        public void actionPerformed( ActionEvent e ) {
            if( isFoldingModel() ) {
                FoldingTaskListModel foldingModel = getFoldingModel();
                int selRow = getSelectedRow();
                if( selRow >= 0 ) {
                    FoldingTaskListModel.FoldingGroup group = foldingModel.getGroupAtRow( selRow );
                    if( null != group && group.isExpanded() != expand ) {
                        group.setExpanded( expand );
                        return;
                    }
                }
            }
            defaultAction.actionPerformed( e );
        }
    }
    
    
    
    private class MyTableHeader extends JTableHeader {
        
        public MyTableHeader( TableColumnModel model ) {
            super( model );
            addMouseListener( new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if( e.getClickCount() != 1 )
                        return;
                    int column = columnAtPoint( e.getPoint() );
                    if( column > 0 && getModel() instanceof TaskListModel ) {
                        ((TaskListModel)getModel()).toggleSort( column );
                        repaint();
                    }
                }
            });
            this.setReorderingAllowed( false );
        }
        
        @Override
        public void setDraggedColumn( TableColumn aColumn ) {
            if( null != aColumn && aColumn.getModelIndex() == 0 )
                return; //don't allow the first column to be dragged
            super.setDraggedColumn( aColumn );
        }
        
        @Override
        public void setDefaultRenderer(TableCellRenderer defaultRenderer) {
            if( !(defaultRenderer instanceof SortingHeaderRenderer) )
                defaultRenderer = new SortingHeaderRenderer( defaultRenderer );
            super.setDefaultRenderer( defaultRenderer );
        }

        @Override
        public void setUI(TableHeaderUI ui) {
            super.setUI(ui);
        }
    
        @Override
        public void setResizingColumn( TableColumn col ) {
            if( null != getResizingColumn() && null == col ) {
                storeColumnState();
            }
            super.setResizingColumn( col );
        }
    }
    
    
    private class DefaultAction extends AbstractAction {
        
        public void actionPerformed(ActionEvent arg0) {
            Task t = getSelectedTask();
            if( null != t ) {
                Action a = Util.getDefaultAction( t );
                if( a.isEnabled() ) {
                    a.actionPerformed( null );
                } else {
                    Toolkit.getDefaultToolkit().beep();
                }
            }
        }
    }
    
    
    private class TaskNavigationAction extends AbstractAction {
        private boolean navigateToNextTask;
        public TaskNavigationAction( boolean navigateToNextTask ) {
            super( navigateToNextTask ? NbBundle.getMessage( TaskListTable.class, "LBL_NextTask" )  //NOI18N
                    : NbBundle.getMessage( TaskListTable.class, "LBL_PrevTask" ) ); //NOI18N
            this.navigateToNextTask = navigateToNextTask;
        }
    
        public void actionPerformed( ActionEvent e ) {
            if( 0 == getModel().getRowCount() )
                return;
            
            int currentRow = getSelectedRow();
            if( currentRow < 0 ) {
                currentRow = 0;
            } else if( !(isFoldingModel() && getFoldingModel().isGroupRow(currentRow)) ) {
                currentRow += (navigateToNextTask ? 1 : -1);
            }
            
            TaskListModel tlm = (TaskListModel)getModel();
            while( true ) {
                if( currentRow < 0 )
                    currentRow = tlm.getRowCount()-1;
                else if( currentRow >= tlm.getRowCount() )
                    currentRow = 0;
                Task t = tlm.getTaskAtRow( currentRow );
                if( null != t ) {
                    getSelectionModel().setSelectionInterval( currentRow, currentRow );
                    scrollRectToVisible( getCellRect( currentRow, 0, true ) );
                    Action a = new OpenTaskAction( t );
                    if( a.isEnabled() ) {
                        a.actionPerformed( e );
                    } else {
                        TaskListTopComponent.findInstance().requestActive();
                    }
                    break;
                } else if( isFoldingModel() ) {
                    FoldingTaskListModel.FoldingGroup fg = getFoldingModel().getGroupAtRow( currentRow );
                    if( !fg.isExpanded() )
                        fg.setExpanded( true );
                }
                currentRow += (navigateToNextTask ? 1 : -1);
            }
        }
    }
    
    
    private class MyTableColumn extends TableColumn {
        private float ratio;
        
        public MyTableColumn( int index ) {
            super( index );
            if( index == TaskListModel.COL_GROUP ) {
                setWidth( isFoldingModel() ? 35 : 20 );
                setMinWidth( isFoldingModel() ? 35 : 20 );
                setMaxWidth( isFoldingModel() ? 35 : 20 );
                setResizable( false );
            } else {
                switch( modelIndex ) {
                case TaskListModel.COL_DESCRIPTION:
                    ratio = 0.65f;
                    break;
                case TaskListModel.COL_LOCATION:
                    ratio = 0.2f;
                    break;
                case TaskListModel.COL_FILE:
                    ratio = 0.1f;
                    break;
                case TaskListModel.COL_LINE:
                    ratio = 0.05f;
                    break;
                }
                ratio = Settings.getDefault().getPreferredColumnWidth( index, isFoldingModel(), ratio );
            }
        }
        
        @Override
        public int getPreferredWidth() {
            int modelIndex = this.getModelIndex();
            if( isFoldingModel() && modelIndex == TaskListModel.COL_GROUP )
                return getWidth();
            
            int totalWidth = getParent().getWidth();
            return (int)(totalWidth*ratio);
        }
        
        public void storeState() {
            float totalWidth = TaskListTable.this.getWidth();
            if( totalWidth > 0 ) {
                ratio = this.getWidth() / totalWidth;
                Settings.getDefault().setPreferredColumnWidth( getModelIndex(), isFoldingModel(), ratio );
            }
        }
    }

    private static class LeftDotRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
                
            super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

            int availableWidth = table.getColumnModel().getColumn( column ).getWidth();
            availableWidth -= table.getIntercellSpacing().getWidth();
            Insets borderInsets = getBorder().getBorderInsets( (Component)this );
            availableWidth -= (borderInsets.left + borderInsets.right);
            String cellText = getText();
            FontMetrics fm = getFontMetrics( getFont() );

            if( fm.stringWidth(cellText) > availableWidth ) {
                setToolTipText( value.toString() );
                String dots = "..."; //NOI18N
                int textWidth = fm.stringWidth( dots );
                int nChars = cellText.length() - 1;
                for( ; nChars > 0; nChars-- ) {
                    textWidth += fm.charWidth( cellText.charAt( nChars ) );

                    if( textWidth > availableWidth ) {
                        break;
                    }
                }

                setText( dots + cellText.substring(nChars + 1) );
            } else {
                setToolTipText( null );
            }

            return this;
        }
    }
    

    private static class TooltipRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
                
            super.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

            int availableWidth = table.getColumnModel().getColumn( column ).getWidth();
            availableWidth -= table.getIntercellSpacing().getWidth();
            Insets borderInsets = getBorder().getBorderInsets( (Component)this );
            availableWidth -= (borderInsets.left + borderInsets.right);
            String cellText = getText();
            FontMetrics fm = getFontMetrics( getFont() );

            if( fm.stringWidth(cellText) > availableWidth ) {
                setToolTipText( cellText );

            } else {
                setToolTipText( null );
            }

            return this;
        }
    }
    
    private class SortingHeaderRenderer implements TableCellRenderer, UIResource {
        
        private TableCellRenderer origRenderer;
        
        private static final String SORT_ASC_ICON = "org/netbeans/modules/tasklist/ui/resources/columnsSortedAsc.gif"; // NOI18N
        private static final String SORT_DESC_ICON = "org/netbeans/modules/tasklist/ui/resources/columnsSortedDesc.gif"; // NOI18N
        
        SortingHeaderRenderer( TableCellRenderer origRenderer ) {
            this.origRenderer = origRenderer;
        }

        /** Overrides superclass method. */
        public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column ) {
            Component comp = origRenderer.getTableCellRendererComponent( table, value, isSelected, hasFocus, row, column );

            if( comp instanceof JLabel ) {
                JLabel label = (JLabel)comp;
                TaskListModel tlm = (TaskListModel)getModel();
                if( column == tlm.getSortingColumnn() ) {
                    label.setIcon( getProperIcon( !tlm.isAscendingSort() ) );
                    label.setHorizontalTextPosition( SwingConstants.LEFT );
                } else {
                    label.setIcon( null );
                }
            }

            return comp;
        }

        private ImageIcon getProperIcon( boolean descending ) {
            if( descending ) {
                return new ImageIcon( org.openide.util.Utilities.loadImage( SORT_DESC_ICON ) );
            } else {
                return new ImageIcon( org.openide.util.Utilities.loadImage( SORT_ASC_ICON ) );
            }
        }
    }
    
    private class PopupAction extends AbstractAction {
        public PopupAction() {
            super( "Popup" ); //NOI18N
        }
    
        public void actionPerformed( ActionEvent e ) {
            Point p = getPositionForPopup();

            if (p == null) {
                //we're going to create a popup menu for the root node
                p = new Point(0, 0);
            }

            JPopupMenu popup = Util.createPopup( TaskListTable.this );
            popup.show( TaskListTable.this, p.x, p.y );
        }
        
        public boolean isEnabled() {
            return TaskListTable.this.isFocusOwner();
        }
    }
    
    private Point getPositionForPopup() {
        int selRow = getSelectedRow();

        if( selRow < 0 ) {
            return null;
        }

        Rectangle rect = getCellRect( selRow, 0, true );

        if( rect == null ) {
            return null;
        }

        return new Point(0, rect.y + rect.height );
    }
}

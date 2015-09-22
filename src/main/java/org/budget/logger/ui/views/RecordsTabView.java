/**
 * 
 */
package org.budget.logger.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractCellEditor;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.xml.bind.ValidationException;

import org.apache.log4j.Logger;
import org.budget.logger.data.model.Category;
import org.budget.logger.data.model.Record;
import org.budget.logger.data.model.Type;
import org.budget.logger.helpers.DateHelper;
import org.budget.logger.ui.AppEvents;
import org.budget.logger.ui.mvc.AppEvent;
import org.budget.logger.ui.mvc.Controller;
import org.budget.logger.ui.mvc.Registry;
import org.budget.logger.ui.mvc.View;
import org.jdesktop.swingx.JXDatePicker;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 15 сент. 2015 г.
 */
public class RecordsTabView extends View {

    private Logger logger = Logger.getLogger(getClass());
    
    private JComboBox<CategoryComboValue> addCat;
    private JComboBox<CategoryComboValue> comboCatSearch;
    private JTable table;
    private RecordsTableModel tableModel;
    private JXDatePicker datePickerFrom;
    private JXDatePicker datePickerTo;
    private JTextField addAmount;
    private JXDatePicker addDate;
    private JComboBox<TypeComboValue> addTypes;
    private JTextField addDesc;

    public RecordsTabView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (AppEvents.Init.equals(event.getType())) {
            onInit();
            return;
        }
    }

    private void onInit() {
        JPanel main = Registry.get("recordsPanel");
        main.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(2, 1));
        topPanel.add(createSearchPanel());
        topPanel.add(createAddPanel());
        main.add(topPanel, BorderLayout.NORTH);

        JScrollPane jscrlp = new JScrollPane(createTable());
        main.add(jscrlp, BorderLayout.CENTER);
    }

    private JTable createTable() {
        tableModel = new RecordsTableModel();
        table = new JTable(tableModel);
        table.getColumnModel().getColumn(0).setPreferredWidth(50);
        table.getColumnModel().getColumn(1).setPreferredWidth(50);
        table.getColumnModel().getColumn(2).setPreferredWidth(50);
        table.getColumnModel().getColumn(3).setPreferredWidth(50);
        table.getColumnModel().getColumn(4).setPreferredWidth(200);
        table.getColumnModel().getColumn(5).setWidth(10);
        table.getColumnModel().getColumn(5).setPreferredWidth(10);
        table.getColumnModel().getColumn(2).setCellRenderer(new AmountCellRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionCell());
        table.getColumnModel().getColumn(5).setCellEditor(new ActionCell());
        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        return table;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        searchPanel.add(new JLabel("Категория:"));
        comboCatSearch = new JComboBox<CategoryComboValue>();
        searchPanel.add(comboCatSearch);

        searchPanel.add(new JLabel("С:"));
        datePickerFrom = new JXDatePicker(DateHelper.getPreviousMonth());
        searchPanel.add(datePickerFrom);
        searchPanel.add(new JLabel("По:"));
        datePickerTo = new JXDatePicker(new Date());
        searchPanel.add(datePickerTo);

        JButton searchBtn = new JButton("Фильтровать");
        searchPanel.add(searchBtn);
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireEvent(AppEvents.DoSearchRecords);
            }
        });
        return searchPanel;
    }

    private JPanel createAddPanel() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        addPanel.add(new JLabel("Дата:"));
        addDate = new JXDatePicker(new Date());
        addPanel.add(addDate);

        TypeComboValue[] typeValues = { new TypeComboValue(Type.OUTCOME), new TypeComboValue(Type.INCOME),
                new TypeComboValue(Type.STORING) };
        addTypes = new JComboBox<RecordsTabView.TypeComboValue>(typeValues);
        addPanel.add(addTypes);

        addPanel.add(new JLabel("Сумма:"));
        addAmount = new JTextField(5);
        addPanel.add(addAmount);

        addCat = new JComboBox<CategoryComboValue>();
        addPanel.add(addCat);

        addPanel.add(new JLabel("Описание:"));
        addDesc = new JTextField(15);
        addPanel.add(addDesc);

        JButton addBtn = new JButton("Добавить");
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Record r = getNewRecord();
                    fireEvent(new AppEvent(AppEvents.DoAddRecord, r));
                } catch (ValidationException ex) {
                    logger.warn(ex.getMessage());
                }
            }
        });
        addPanel.add(addBtn);
        return addPanel;
    }

    private Record getNewRecord() throws ValidationException {
        validateEmpty(addAmount);
        validateEmpty(addDesc);
        Record r = new Record();
        try {
            r.setAmount(Double.parseDouble(addAmount.getText()));
        } catch (Exception e) {
            throw new ValidationException("amount not valid");
        }
        r.setDesc(addDesc.getText());
        TypeComboValue typeValue = (TypeComboValue) addTypes.getSelectedItem();
        r.setType(typeValue.getValue());
        CategoryComboValue catValue = (CategoryComboValue) addCat.getSelectedItem();
        r.setCategory(catValue.getValue().getName());
        r.setDate(addDate.getDate());
        addAmount.setText("");
        addDesc.setText("");
        return r;
    }
    
    private void validateEmpty(JTextField field) throws ValidationException {
        String value = field.getText();
        if (null == value || "".equals(value.trim())) {
            field.setBackground(new Color(255, 200, 200));
            throw new ValidationException("field is empty");
        } else {
            field.setBackground(new Color(255, 255, 255));
        }
    }
    
    public void setCategories(List<Category> categories) {
        List<CategoryComboValue> list = new ArrayList<CategoryComboValue>();
        CategoryComboValue def = null;
        for (Category c : categories) {
            CategoryComboValue value = new CategoryComboValue(c);
            list.add(value);
            if (c.getDef()) {
                def = value;
            }
        }
        comboCatSearch.setModel(new DefaultComboBoxModel<CategoryComboValue>(list.toArray(new CategoryComboValue[0])));
        addCat.setModel(new DefaultComboBoxModel<CategoryComboValue>(list.toArray(new CategoryComboValue[0])));
        if (null != def) {
            comboCatSearch.setSelectedItem(def);
            addCat.setSelectedItem(def);
        }
    }

    public void setData(List<Record> data) {
        tableModel.setData(data);
    }
    
    public void deleteFromStore(Record r) {
        tableModel.deleteRow(r);
        table.updateUI();
    }

    public Category getSearchCategory() {
        CategoryComboValue value = (CategoryComboValue) comboCatSearch.getSelectedItem();
        return null != value ? value.getValue() : null;
    }

    public Date getSearchDateFrom() {
        return datePickerFrom.getDate();
    }

    public Date getSearchDateTo() {
        return datePickerTo.getDate();
    }

    private static class TypeComboValue {
        private String label;
        private Type value;

        public TypeComboValue(Type value) {
            this.label = getTypeString(value);
            this.value = value;
        }

        public Type getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class CategoryComboValue {
        private String label;
        private Category value;

        public CategoryComboValue(Category value) {
            this.label = value.getName();
            this.value = value;
        }

        public Category getValue() {
            return value;
        }

        @Override
        public String toString() {
            return label;
        }
    }

    private static class RecordsTableModel implements TableModel {

        private Set<TableModelListener> listeners = new HashSet<TableModelListener>();

        private List<Record> data = new ArrayList<Record>();

        private static final DateFormat DF = new SimpleDateFormat("yyyy.MM.dd");

        public void setData(List<Record> data) {
            this.data = data;
            for (TableModelListener l : listeners) {
                TableModelEvent e = new TableModelEvent(this);
                l.tableChanged(e);
            }
        }
        
        public void deleteRow(Record r) {
            this.data.remove(r);
            for (TableModelListener l : listeners) {
                TableModelEvent e = new TableModelEvent(this);
                l.tableChanged(e);
            }
        }

        public Record getRow(int rowIdx) {
            return data.get(rowIdx);
        }

        @Override
        public int getRowCount() {
            return data.size();
        }

        @Override
        public int getColumnCount() {
            return 6;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
            case 0:
                return "Дата";
            case 1:
                return "Тип";
            case 2:
                return "Сумма";
            case 3:
                return "Категория";
            case 4:
                return "Описание";
            case 5:
                return "";
            }
            return "";
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            Record r = data.get(rowIndex);
            switch (columnIndex) {
            case 0:
                return DF.format(r.getDate());
            case 1:
                return getTypeString(r.getType());
            case 2:
                return r.getAmount();
            case 3:
                return r.getCategory();
            case 4:
                return r.getDesc();
            case 5:
                return "";
            }
            return "";

        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == 5;
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

    }

    private class AmountCellRenderer extends DefaultTableCellRenderer {
        private static final long serialVersionUID = 1L;

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super
                    .getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            Record r = tableModel.getRow(row);
            int amount = r.getAmount().intValue();
            label.setText("" + amount);
            switch (r.getType()) {
            case INCOME:
                label.setForeground(new Color(0, 0, 170));
                return label;
            case OUTCOME:
                label.setForeground(new Color(170, 0, 0));
                return label;
            case STORING:
                label.setForeground(new Color(0, 170, 0));
                return label;
            }
            return label;
        }
    }

    private class ActionCell extends AbstractCellEditor implements TableCellEditor, TableCellRenderer {
        private static final long serialVersionUID = 1L;
        private JPanel panel;
        private JLabel labelEdit;
        private JLabel labelDelete;
        private Record record;

        public ActionCell() {
            panel = new JPanel();
            labelEdit = new JLabel();
            labelEdit.setIcon(ImageHelper.EDIT);
            labelEdit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            panel.add(labelEdit);
            labelEdit.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    fireEvent(new AppEvent(AppEvents.DoEditRecord, record));
                }
            });
            labelDelete = new JLabel();
            labelDelete.setIcon(ImageHelper.DELETE);
            labelDelete.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            panel.add(labelDelete);
            labelDelete.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    fireEvent(new AppEvent(AppEvents.DoDeleteRecord, record));
                }
            });
        }

        private void updateData(Record record, boolean isSelected, JTable table, int row) {
            this.record = record;
            if (isSelected) {
                panel.setBackground(table.getSelectionBackground());
            } else {
                Color alternateColor = new Color(242, 242, 242);
                if (null != alternateColor && row % 2 != 0) {
                    panel.setBackground(alternateColor);
                } else {
                    panel.setBackground(Color.WHITE);
                }
            }
        }

        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            Record record = tableModel.getRow(row);
            updateData(record, true, table, row);
            return panel;
        }

        public Object getCellEditorValue() {
            return null;
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            Record record = tableModel.getRow(row);
            updateData(record, isSelected, table, row);
            return panel;
        }
    }

    private static String getTypeString(Type type) {
        switch (type) {
        case INCOME:
            return "Приход";
        case OUTCOME:
            return "Расход";
        case STORING:
            return "Накопление";
        }
        return "";
    }
}

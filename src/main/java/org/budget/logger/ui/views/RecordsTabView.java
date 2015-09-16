/**
 * 
 */
package org.budget.logger.ui.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import org.budget.logger.data.model.Category;
import org.budget.logger.data.model.Record;
import org.budget.logger.data.model.Type;
import org.budget.logger.helpers.DateHelper;
import org.budget.logger.ui.AppEvents;
import org.budget.logger.ui.mvc.AppEvent;
import org.budget.logger.ui.mvc.Controller;
import org.budget.logger.ui.mvc.Dispatcher;
import org.budget.logger.ui.mvc.Registry;
import org.budget.logger.ui.mvc.View;
import org.jdesktop.swingx.JXDatePicker;

/**
 * @author <sergey.kanshin@gmail.com>
 * @date 15 сент. 2015 г.
 */
public class RecordsTabView extends View {

    private JComboBox<CategoryComboValue> comboCatAdd;
    private JComboBox<CategoryComboValue> comboCatSearch;
    private RecordsTableModel tableModel;
    private JXDatePicker datePickerFrom;
    private JXDatePicker datePickerTo;

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
        JTable table = new JTable(tableModel);
        table.getColumnModel().getColumn(2).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                Record r = tableModel.getRow(row);
                int amount = r.getAmount().intValue();
                JLabel label = null;
                switch(r.getType()) {
                case INCOME:
                    label = new JLabel("" + amount);
                    label.setForeground(new Color(0, 0, 170));
                    return label;
                case OUTCOME:
                    label = new JLabel("-" + amount);
                    label.setForeground(new Color(170, 0, 0));
                    return label;
                case STORING:
                    label = new JLabel("" + amount);
                    label.setForeground(new Color(0, 170, 0));
                    return label;
                }
                return null;
            }
        });
        table.getColumnModel().getColumn(5).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                return new JButton(ImageHelper.DELETE);
            }
        });
        
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
                Dispatcher.forwardEvent(AppEvents.DoSearchRecords);
            }
        });
        return searchPanel;
    }

    private JPanel createAddPanel() {
        JPanel addPanel = new JPanel();
        addPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        addPanel.add(new JLabel("Дата:"));
        JXDatePicker datePicker = new JXDatePicker(new Date());
        addPanel.add(datePicker);
        TypeComboValue[] typeValues = { new TypeComboValue(Type.OUTCOME), new TypeComboValue(Type.INCOME),
                new TypeComboValue(Type.STORING) };
        JComboBox<TypeComboValue> typesCombo = new JComboBox<RecordsTabView.TypeComboValue>(typeValues);
        addPanel.add(typesCombo);
        comboCatAdd = new JComboBox<CategoryComboValue>();
        addPanel.add(comboCatAdd);
        addPanel.add(new JLabel("Сумма:"));
        JTextField sumField = new JTextField(5);
        addPanel.add(sumField);

        addPanel.add(new JLabel("Описание:"));
        JTextField descField = new JTextField(15);
        addPanel.add(descField);

        JButton addBtn = new JButton("Добавить");
        addPanel.add(addBtn);

        return addPanel;
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
        comboCatAdd.setModel(new DefaultComboBoxModel<CategoryComboValue>(list.toArray(new CategoryComboValue[0])));
        if (null != def) {
            comboCatSearch.setSelectedItem(def);
            comboCatAdd.setSelectedItem(def);
        }
    }

    public void setData(List<Record> data) {
        tableModel.setData(data);
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

        public String getLabel() {
            return label;
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

        public String getLabel() {
            return label;
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
                return "Действие";
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
            return false;
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

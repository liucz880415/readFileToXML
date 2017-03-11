package com.lcz.file;

import com.lcz.file.entity.GenerateXmlParams;
import org.apache.commons.lang.StringUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by liuchuanzhu on 2017-03-05.
 */
public class GenerateXmlApp extends JFrame implements ActionListener {

    private MJTextField templateText = new MJTextField();
    private MJTextField excelText = new MJTextField();
    private MJTextField xmlPathText = new MJTextField("D:\\data\\");
    private MJTextField fileNamePreText = new MJTextField("claim");
    private MJTextField fileNameText = new MJTextField("交易流水号");

    private MJTextField batchNoText = new MJTextField();

    private MJTextField archiveNoPreText = new MJTextField("1001");
    private MJTextField archiveNoText = new MJTextField();
    private MJTextField archiveNoPostText = new MJTextField("1000000001");

    private GenerateXmlParams generateXmlParams = new GenerateXmlParams();

    private JLabel errorLabel = new JLabel();


    public GenerateXmlApp(String title) {
        //标题栏
        super(title);
        //大小
        setSize(650, 500);
        // 居中
        setLocationRelativeTo(null);
        //默认关闭操作
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //禁止修改大小
        setResizable(false);
        //初始化文本框与按钮
        generateInterface();
        //显示
        setVisible(true);
    }

    /**
     * 初始化文本框和按钮，生成界面
     */
    private void generateInterface() {

        Box contentBox = Box.createVerticalBox();//纵向
        contentBox.add(Box.createVerticalStrut(10));
        contentBox.add(addLine("xml模板路径：", templateText));
        contentBox.add(Box.createVerticalStrut(10));
        contentBox.add(addLine("excel路径：", excelText));
        contentBox.add(Box.createVerticalStrut(10));
        contentBox.add(addLine("xml存放路径：", xmlPathText));
        contentBox.add(Box.createVerticalStrut(10));

        //创建特殊规则
        contentBox.add(addParticularLine("文件名规则：", fileNamePreText, fileNameText, null));
        contentBox.add(Box.createVerticalStrut(10));
        contentBox.add(addParticularLine("batch_no 规则：", null, batchNoText, null));
        contentBox.add(Box.createVerticalStrut(10));
        contentBox.add(addParticularLine("archive_no 规则：", archiveNoPreText, archiveNoText, archiveNoPostText));

        contentBox.add(Box.createVerticalStrut(20));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setPreferredSize(new Dimension(600, 150));
        JButton generateBtn = new JButton("生成xml");
        generateBtn.setPreferredSize(new Dimension(100, 30));

        generateBtn.addActionListener(this);

        buttonPanel.add(generateBtn);

        buttonPanel.add(Box.createVerticalStrut(10));
        //添加错误提示
        errorLabel.setForeground(Color.red);
        errorLabel.setPreferredSize(new Dimension(100, 50));
        buttonPanel.add(errorLabel);

        contentBox.add(buttonPanel);

        add(contentBox);
    }

    private JPanel addParticularLine(String ruleLabel, MJTextField pre, MJTextField textField, MJTextField post) {
        JPanel specialPanel = new JPanel();
        specialPanel.setLayout(new BoxLayout(specialPanel, BoxLayout.X_AXIS));
        JLabel specialLabel = new JLabel(ruleLabel, JLabel.RIGHT);
        specialLabel.setPreferredSize(new Dimension(120, 20));
        specialPanel.add(specialLabel);

        specialPanel.add(Box.createHorizontalStrut(10));

        if (pre != null) {
            pre.setPreferredSize(new Dimension(50, 20));
            specialPanel.add(pre);
        }
        if (pre != null && textField != null) {
            JLabel specialLabel1 = new JLabel(" + ", JLabel.CENTER);
            specialLabel1.setPreferredSize(new Dimension(20, 20));
            specialPanel.add(specialLabel1);
        }

        if (textField != null) {
            int width = (pre == null && post == null) ? 200 : 50;
            textField.setPreferredSize(new Dimension(width, 20));
            specialPanel.add(textField);
        }

        if (post != null) {
            JLabel specialLabel2 = new JLabel(" + ", JLabel.CENTER);
            specialLabel2.setPreferredSize(new Dimension(20, 20));
            specialPanel.add(specialLabel2);

            post.setPreferredSize(new Dimension(50, 20));
            specialPanel.add(post);
        }

        specialPanel.add(Box.createHorizontalStrut(20));
        return specialPanel;
    }

    private JPanel addLine(String label, JTextField textField) {
        JPanel templatePanel = new JPanel();
        templatePanel.setLayout(new BoxLayout(templatePanel, BoxLayout.X_AXIS));
        JLabel templateLabel = new JLabel(label, JLabel.RIGHT);
        templateLabel.setPreferredSize(new Dimension(120, 20));
        templatePanel.add(templateLabel);

        templatePanel.add(Box.createHorizontalStrut(10));

        textField.setEditable(false);
        textField.setPreferredSize(new Dimension(200, 20));
        templatePanel.add(textField);

        templatePanel.add(Box.createHorizontalStrut(10));

        JButton selectBtn = new JButton("选择");
        selectBtn.setPreferredSize(new Dimension(60, 20));
        selectBtn.addActionListener((ActionEvent e) -> {

            JFileChooser jfc = new JFileChooser();

            jfc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            jfc.showDialog(new JLabel(), "选择");

            File file = jfc.getSelectedFile();
            if (file != null) {
                if (textField == templateText) {// xml
                    if (file.isDirectory()) {
                        errorLabel.setText("不能是文件夹，必须是xml文件！");
                    } else if (!file.getAbsolutePath().endsWith(".xml")) {
                        errorLabel.setText("必须是.xml文件！");
                    } else {
                        templateText.setText(file.getAbsolutePath());
                        generateXmlParams.setXmlTemplateFile(file);
                        errorLabel.setText("");
                    }
                } else if (textField == excelText) {//xls xlsx
                    if (file.isDirectory()) {
                        errorLabel.setText("不能是文件夹，必须是excel文件！");
                    } else if (!file.getAbsolutePath().endsWith(".xls") && !file.getAbsolutePath().endsWith(".xlsx")) {//
                        errorLabel.setText("必须是.xls或.xlsx文件！");
                    } else {
                        excelText.setText(file.getAbsolutePath());
                        generateXmlParams.setExcelFile(file);
                        errorLabel.setText("");
                    }
                } else {
                    if (!file.isDirectory()) {//不是目录
                        errorLabel.setText("必须是文件目录！");
                    } else {
                        xmlPathText.setText(file.getAbsolutePath());
                        generateXmlParams.setXmlStorePath(file.getAbsolutePath());
                        errorLabel.setText("");
                    }
                }
            }
        });
        templatePanel.add(selectBtn);

        templatePanel.add(Box.createHorizontalStrut(20));
        return templatePanel;
    }

    public boolean isNumericString(String str) {
        Pattern p = Pattern.compile("^[0-9]+$");
        Matcher m = p.matcher(str);
        return m.matches();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        boolean isValidated = true;
        String source = e.getActionCommand();
        if (source.equals("生成xml")) {
            String templatePath = templateText.getText();
            String excelPath = excelText.getText();
            String xmlPath = xmlPathText.getText();
            String fileNamePre = fileNamePreText.getText();
            String fileName = fileNameText.getText();

            //batch_no
            String batchNo = batchNoText.getText();
            //archive_no
            String archiveNOPre = archiveNoPreText.getText();
            String archiveNo = archiveNoText.getText();
            String archiveNoPost = archiveNoPostText.getText();

            if (StringUtils.isBlank(templatePath)) {
                errorLabel.setText("xml模板路径不能为空！");
                isValidated = false;
            } else if (StringUtils.isBlank(excelPath)) {
                errorLabel.setText("excel路径不能为空！");
                isValidated = false;
            }
            if (!isNumericString(archiveNoPost)) {
                errorLabel.setText("archive_no必须是数字！");
                isValidated = false;
            }

            if (isValidated) {
                generateXmlParams.setXmlStorePath(xmlPath);
                generateXmlParams.setArchiveNOPre(archiveNOPre);
                generateXmlParams.setArchiveNo(archiveNo);
                generateXmlParams.setArchiveNoPost(archiveNoPost);
                generateXmlParams.setBatchNo(batchNo);
                generateXmlParams.setFileName(fileName);
                generateXmlParams.setFileNamePre(fileNamePre);

                new ReadFile().parseXml(generateXmlParams);
                errorLabel.setText("excel转换xml格式成功！");
                errorLabel.setForeground(Color.green);
            }
        }
    }

    public static void main(String[] args) {
        new GenerateXmlApp("excel转xml");
    }
}

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.*;

public class MainFrame extends JFrame {
    private JPanel rootPanel;
    private JPanel menuScreen;
    private JPanel gameScreen;
    private JButton nextButton;
    private JButton selectButton;
    private JButton prevButton;
    private BoardPanel previewPanel;
    private JLabel menuLayoutName;
    private JButton menuButton;
    private JButton restartButton;
    private GameBoardPanel gamePanel;

    Layout[] layouts = new Layout[]{
            new Layout(new int[][]{
                    {0, 0, 2, 2, 2, 0, 0},
                    {0, 0, 2, 2, 2, 0, 0},
                    {2, 2, 2, 2, 2, 2, 2},
                    {2, 2, 2, 1, 2, 2, 2},
                    {2, 2, 2, 2, 2, 2, 2},
                    {0, 0, 2, 2, 2, 0, 0},
                    {0, 0, 2, 2, 2, 0, 0}
            }, "Стандарт")
    };
    int activeLayout = 0;

    private void keepPanelSquare(JPanel innerPanel, JPanel container, double aspect) {
        int w = container.getWidth();
        int h = container.getHeight();

        if (h < container.getMinimumSize().height || w < container.getMinimumSize().width) {
            w = container.getMinimumSize().width;
            h = container.getMinimumSize().height;
        }

        int size = (int) (Math.min(w, h) * aspect);
        innerPanel.setPreferredSize(new Dimension(size, size));
        container.revalidate();
    }

    private void initPreviewPanel() {
        previewPanel.reset(layouts[activeLayout].layout);
        menuLayoutName.setText(layouts[activeLayout].name);
    }

    private void readLayouts() {
        JSONParser parser = new JSONParser();
        Layout[] layouts = null;

        try {
            InputStream in = getClass().getClassLoader().getResourceAsStream("layouts.json");
            Object json = parser.parse(new InputStreamReader(in, "UTF-8"));

            JSONArray layoutsJSON = (JSONArray) json;
            int amount = layoutsJSON.size();
            layouts = new Layout[amount + 1];
            layouts[0] = this.layouts[0];

            int i = 1;
            for (Object el : layoutsJSON) {
                JSONObject layoutObj = (JSONObject) el;

                String name = (String) layoutObj.get("name");
                System.out.println(name);

                JSONArray layoutJSON = (JSONArray) layoutObj.get("layout");
                int[][] layout = readJSONLayout(layoutJSON);

                layouts[i] = new Layout(layout, name);
                i++;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (layouts != null) {
            this.layouts = layouts;
        }
    }

    private int[][] readJSONLayout(JSONArray array2D) {
        int size = array2D.size();
        int[][] res = new int[size][size];
        int i = 0;
        int j = 0;

        for (Object ar : array2D) {
            JSONArray array = (JSONArray) ar;

            for (Object label : array) {
                res[i][j] = (int) (long) label;
                j++;
            }

            i++;
            j = 0;
        }

        return res;
    }

    MainFrame() {
        setTitle("Peg Solitaire");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setContentPane(rootPanel);

        readLayouts();

        initPreviewPanel();

        menuScreen.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                keepPanelSquare(previewPanel, menuScreen, .75);
            }
        });

        gameScreen.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                keepPanelSquare(gamePanel, gameScreen, .80);
            }
        });

        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.reset(layouts[activeLayout].layout);

                CardLayout cl = (CardLayout) (rootPanel.getLayout());
                cl.show(rootPanel, "game");
            }
        });

        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gamePanel.reset(layouts[activeLayout].layout);
            }
        });

        menuButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CardLayout cl = (CardLayout) (rootPanel.getLayout());
                cl.show(rootPanel, "menu");
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activeLayout = activeLayout + 1 > layouts.length - 1 ? 0 : activeLayout + 1;
                previewPanel.reset(layouts[activeLayout].layout);
                menuLayoutName.setText(layouts[activeLayout].name);
            }
        });

        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                activeLayout = activeLayout - 1 < 0 ? layouts.length - 1 : activeLayout - 1;
                previewPanel.reset(layouts[activeLayout].layout);
                menuLayoutName.setText(layouts[activeLayout].name);
            }
        });
    }

    private void createUIComponents() {
        previewPanel = new BoardPanel(new int[][]{
                {0, 0, 2, 2, 2, 0, 0},
                {0, 0, 2, 2, 2, 0, 0},
                {2, 2, 2, 2, 2, 2, 2},
                {2, 2, 2, 1, 2, 2, 2},
                {2, 2, 2, 2, 2, 2, 2},
                {0, 0, 2, 2, 2, 0, 0},
                {0, 0, 2, 2, 2, 0, 0}
        });

        gamePanel = new GameBoardPanel(new int[][]{
                {0, 0, 2, 2, 2, 0, 0},
                {0, 0, 2, 2, 2, 0, 0},
                {2, 2, 2, 2, 2, 2, 2},
                {2, 2, 2, 1, 2, 2, 2},
                {2, 2, 2, 2, 2, 2, 2},
                {0, 0, 2, 2, 2, 0, 0},
                {0, 0, 2, 2, 2, 0, 0}
        });
    }

    class Layout {
        public int[][] layout;
        public String name;

        Layout(int[][] layout, String name) {
            this.layout = layout;
            this.name = name;
        }
    }

    public static void main(String[] args) {
        JFrame mainFrame = new MainFrame();
        mainFrame.pack();
        mainFrame.setLocationRelativeTo(null);
        mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        mainFrame.setVisible(true);
    }
}

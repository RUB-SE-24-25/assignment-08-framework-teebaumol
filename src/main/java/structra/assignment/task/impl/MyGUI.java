package structra.assignment.task.impl;

import structra.assignment.framework.llm.MachineLearningModel;
import structra.assignment.framework.llm.gen.questions.MultipleChoiceTarget;
import structra.assignment.framework.llm.gen.questions.OpenQuestionTarget;
import structra.assignment.framework.llm.gen.questions.RandomTargetProvider;
import structra.assignment.framework.llm.gen.questions.TargetProvider;
import structra.assignment.framework.llm.model.Mimic;
import structra.assignment.framework.model.answer.base.Answer;
import structra.assignment.framework.model.question.base.Question;
import structra.assignment.framework.model.question.concrete.OpenAnswerQuestion;
import structra.assignment.framework.provide.ModelQuestionProvider;
import structra.assignment.framework.provide.QuestionProvider;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class MyGUI {

    static String result;

    static JLabel label;
    static JButton button;
    static JTextField textField;
    static JPanel panel = new JPanel();

    static TargetProvider provider = new RandomTargetProvider(new OpenQuestionTarget(Mimic.OPEN_ANSWER));
    static MyKeyProvider keyProvider = new MyKeyProvider();
    static private final MachineLearningModel mimic = new Mimic(keyProvider);
    static QuestionProvider questionProvider = new ModelQuestionProvider(mimic, provider, new ArrayList<>());

    static CompletableFuture<Question<?>> future;

    private static void createAndShowGUI() {

        JFrame frame = new JFrame("Quiz");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        panel.setLayout(new BorderLayout());

        label = new JLabel("Welcome to my Quiz",SwingConstants.CENTER);
        panel.add(label,BorderLayout.CENTER);

        frame.getContentPane().add(panel,BorderLayout.CENTER);

        button = new JButton("Start!");
        button.setPreferredSize(new Dimension(150,0));

        button.addActionListener((e)->buttonAction());
        frame.getContentPane().add(button,BorderLayout.WEST);

        textField = new JTextField("Type your answers here!");
        textField.setPreferredSize(new Dimension(200, 40));
        textField.addActionListener((e)->textFieldAction());
        frame.getContentPane().add(textField,BorderLayout.SOUTH);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth();
        double height = screenSize.getHeight();
        int frameWidth = (int) (width / 2);
        int frameHeight = (int) (height / 2);
        frame.setLocation((int) (width - frameWidth) / 2, (int) (height - frameHeight) / 2);
        frame.setSize(frameWidth, frameHeight);

        frame.setVisible(true);

    }

    public static void main(String[] args) {
        // Schedule a job for the event-dispatching thread:
        // creating and showing this application's GUI.
        SwingUtilities.invokeLater(MyGUI::createAndShowGUI);
    }

    static void buttonAction(){
        future = questionProvider.next();
        result = labelFormat(future);
        label.setText(result);
        button.setText("Next Question!");
        textField.setText("");
    }

    static void textFieldAction(){

        textField.setText(future.join().getAnswers()[0].getText());

    }

    static String labelFormat(CompletableFuture<Question<?>> future){;
        switch(future.join().getType()) {

            case OPEN_ANSWER:

                result = "<html>" + "Question: " + future.join().getText()
                        + "<br/>" + "Difficulty: " + future.join().getDifficulty()
                        + "<br/>" + "Points possible:" + future.join().getPointsPossible()
                        + "<br/>" + "Explanation: " + future.join().getExplanation()
                        + "<br/>" + "Answer: " + future.join().getAnswers()[0].getText();
                return result;

            case MULTIPLE_CHOICE:

                result = "<html>" + "Question: " + future.join().getText()                  // <br/> creates a new line
                        + "<br/>" + "Difficulty: " + future.join().getDifficulty()          // &emsp; creates a space because
                        + "<br/>" + "Points possible:" + future.join().getPointsPossible()  // spaces get ignored in html format
                        + "<br/>" + "Explanation: " + future.join().getExplanation()
                        + "<br/>" + "Answers:"
                        + "<br/> &emsp;&emsp;" + future.join().getAnswers()[0].getText()
                        + "<br/> &emsp;&emsp;" + future.join().getAnswers()[1].getText()
                        + "<br/> &emsp;&emsp;" + future.join().getAnswers()[2].getText()
                        + "<br/> &emsp;&emsp;" + future.join().getAnswers()[3].getText();
                return result;

            default:
                return null;
        }
    }
}

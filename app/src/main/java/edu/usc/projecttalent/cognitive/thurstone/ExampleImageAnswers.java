package edu.usc.projecttalent.cognitive.thurstone;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TableRow;

import java.util.LinkedList;
import java.util.Queue;

import edu.usc.projecttalent.cognitive.BaseActivity;
import edu.usc.projecttalent.cognitive.R;
import edu.usc.projecttalent.cognitive.databinding.ExampleAnswersBinding;
import edu.usc.projecttalent.cognitive.model.Answer;
import edu.usc.projecttalent.cognitive.model.Block;
import edu.usc.projecttalent.cognitive.model.Section;
import edu.usc.projecttalent.cognitive.model.Survey;
import edu.usc.projecttalent.cognitive.reasoning.SecAR_Activity;

/**
 * Thurstone example activity.
 *
 * @author Anindya Dutta
 * @version 2.0
 */

public class ExampleImageAnswers extends BaseActivity {

    private View oldView;
    private int score;
    private Answer mAnswer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Section mSection = new Section(getString(R.string.thurs_example));
        Block mBlock = new Block(1);
        Drawable highlight = ContextCompat.getDrawable(this, R.drawable.highlight);

        Resources res = getResources();
        TypedArray questions = res.obtainTypedArray(R.array.th_practice);
        Queue<THItem> mQueue = new LinkedList<>();
        for (int i = 0; i < questions.length(); i++) {
            mQueue.add(new THItem(res.obtainTypedArray(questions.getResourceId(i, 0))));
        }

        ExampleAnswersBinding binding = DataBindingUtil.setContentView(this, R.layout.example_answers);
        binding.setItem(mQueue.remove());
        Button btn = (Button) findViewById(R.id.next);
        btn.setEnabled(false);

        TableRow options = (TableRow) findViewById(R.id.options);
        for (int i = 0; i < options.getChildCount(); i++) {
            (options.getChildAt(i)).setOnClickListener(v -> {
                v.setBackground(highlight);
                if (oldView != null)
                    oldView.setBackground(null);
                oldView = v;
                btn.setEnabled(true);
            });
        }

        mAnswer = new Answer();

        btn.setOnClickListener(v -> {
            if (oldView != null) {
                THItem question = binding.getItem();
                boolean correct = false;
                if (options.indexOfChild(oldView) == question.getAnsOption()) {
                    score++; //correct answer.
                    correct = true;
                }
                mAnswer.endAnswer(oldView == null ? -99 : options.indexOfChild(oldView) + 1, correct); //to shift indices from 1-5.
                mBlock.addAnswer(mAnswer);
                if (oldView != null)
                    oldView.setBackground(null);
                oldView = null;
                if (!mQueue.isEmpty()) {
                    mAnswer = new Answer();
                    binding.setItem(mQueue.remove());
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    mBlock.endBlock(score);
                    mSection.addBlock(mBlock);
                    mSection.endSection(); //end this section.
                    Survey.getSurvey().addSection(mSection);

                    if (score == 0) {
                        AlertDialog dialog = builder.setMessage(R.string.pressnext)
                                .setNeutralButton(R.string.next, (d, which) -> startActivityForResult(new Intent(this, SecAR_Activity.class), 1))
                                .setCancelable(false)
                                .create();
                        dialog.show();
                    } else {
                        View view = getLayoutInflater().inflate(R.layout.begin_real_test, null);
                        (view.findViewById(R.id.beginBtn)).setOnClickListener(v2 ->
                                startActivityForResult(new Intent(this, TestImageChange.class), 1));
                        builder.setView(view);
                        AlertDialog dialog = builder.setView(view)
                                .setCancelable(false)
                                .create();
                        dialog.show();
                    }
                }
            }
        });
    }
}

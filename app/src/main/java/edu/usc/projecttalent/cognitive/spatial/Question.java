package edu.usc.projecttalent.cognitive.spatial;

import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.Queue;

import edu.usc.projecttalent.cognitive.EndTest;
import edu.usc.projecttalent.cognitive.QuestionActivity;
import edu.usc.projecttalent.cognitive.R;
import edu.usc.projecttalent.cognitive.Timer;
import edu.usc.projecttalent.cognitive.databinding.ActivitySpQuestionBinding;
import edu.usc.projecttalent.cognitive.model.Answer;
import edu.usc.projecttalent.cognitive.model.Block;
import edu.usc.projecttalent.cognitive.model.Section;
import edu.usc.projecttalent.cognitive.reasoning.Item;

/**
 * Block-adaptive test for Spatial 2D Visualization.
 * Show block 3 first. Based on score, show one of blocks 1, 2, 4 or 5.
 *
 * @author Anindya Dutta
 */

public class Question extends QuestionActivity {

    private boolean mFtWarn;
    private Queue<Item> mQueue;
    private View oldView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mSection = new Section(getString(R.string.sp_section_title));  //make new section.
        mScore = 0; //reset score at the beginning of block.
        mSkipClass = EndTest.class;

        mTimer = Timer.getTimer(3);
        prepareFilter();

        mBlock = new Block(3); //first block is Block 3.
        mFtWarn = true; //for FTU.

        Resources res = getResources();
        TypedArray questions = res.obtainTypedArray(R.array.sp_3); //all questions of Set 3.
        //get questions for set 3.
        mQueue = new LinkedList<>();
        for (int i = 0; i < questions.length(); i++) {
            mQueue.add(new Item(res.obtainTypedArray(questions.getResourceId(i, 0)))); //each question. sp_31 .. sp_33.
        }
        ActivitySpQuestionBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sp_question);
        binding.setItem(mQueue.remove());
        mAnswer = new Answer();
        mTimer.startTimer();

        LinearLayout options = (LinearLayout) findViewById(R.id.options);
        for (int i = 1; i < options.getChildCount(); i++) {
            (options.getChildAt(i)).setOnClickListener(v -> {
                v.setPadding(2, 2, 2, 2);
                v.setBackgroundColor(ContextCompat.getColor(mContext, R.color.black));
                if (oldView != null)
                    oldView.setBackground(null);
                oldView = v;
            });
        }

        Button next = (Button) findViewById(R.id.next);
        next.setOnClickListener(v -> {
            if (oldView == null && mFtWarn) {
                mFtWarn = false;
                sendBroadcast(new Intent(Timer.NOANSWER));
                return;
            }
            Item question = binding.getItem();
            boolean correct = false;
            if (options.indexOfChild(oldView) == question.getAnsOption()) {
                mScore++; //correct answer.
                correct = true;
            }
            mAnswer.endAnswer(oldView == null ? -99 : options.indexOfChild(oldView), correct);
            mBlock.addAnswer(mAnswer);
            if (oldView != null)
                oldView.setBackground(null);
            oldView = null;
            if (!mQueue.isEmpty()) {
                mAnswer = new Answer();
                binding.setItem(mQueue.remove());
                mTimer.startTimer();
                mFtWarn = true;
                return;
            }
            mBlock.endBlock(mScore);
            mSection.addBlock(mBlock);
            if (mSection.getBlockSize() == 1) { //get next block.
                int block = nextSet();
                mBlock = new Block(getBlockId(block));
                TypedArray questions1 = res.obtainTypedArray(block); //all questions of Set X.
                mQueue = new LinkedList<>();
                for (int i = 0; i < questions1.length(); i++) {
                    mQueue.add(new Item(res.obtainTypedArray(questions1.getResourceId(i, 0)))); //each question. sp_x1 .. sp_x3.
                }
                mScore = 0;
                binding.setItem(mQueue.remove());
                mTimer.startTimer();
                mFtWarn = true;
            } else {
                finishSection();
            }
        });
    }

    private int nextSet() {
        switch (mScore) {
            case 0:
                return R.array.sp_1;
            case 1:
                return R.array.sp_2;
            case 2:
                return R.array.sp_4;
            default:
                return R.array.sp_5;
        }
    }
}
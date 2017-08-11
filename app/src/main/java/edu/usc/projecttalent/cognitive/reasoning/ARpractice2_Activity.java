package edu.usc.projecttalent.cognitive.reasoning;

import android.content.Intent;
import android.content.res.TypedArray;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.LinkedList;
import java.util.Queue;

import edu.usc.projecttalent.cognitive.BaseActivity;
import edu.usc.projecttalent.cognitive.R;
import edu.usc.projecttalent.cognitive.databinding.ActivitySecArBinding;

public class ARpractice2_Activity extends BaseActivity {

    View oldView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Queue<ARExample> exampleList = new LinkedList<>();
        TypedArray arr = getResources().obtainTypedArray(R.array.ar_ex_3);
        exampleList.add(new ARExample(getString(R.string.practiceq2), arr, "", false));
        exampleList.add(new ARExample(getString(R.string.ar_instr_header), arr, getString(R.string.pr_explain), true));

        ActivitySecArBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_sec_ar_);
        binding.setItem(exampleList.remove());

        Button button = (Button) findViewById(R.id.next);
        button.setOnClickListener(v -> {
            if (!exampleList.isEmpty()) {
                binding.setItem(exampleList.remove());
            } else {
                Intent intent = new Intent(this, ARIntro_Activity.class);
                startActivityForResult(intent, 1);
            }
        });

        LinearLayout options = (LinearLayout) findViewById(R.id.options);
        for (int i = 0; i < options.getChildCount(); i++) {
            options.getChildAt(i).setOnClickListener(v -> {
                if (!exampleList.isEmpty()) {
                    v.setPadding(1, 1, 1, 1);
                    v.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                    if (oldView != null)
                        oldView.setBackground(null);
                    oldView = v;
                }
            });
        }
    }
}

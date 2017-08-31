package edu.usc.projecttalent.cognitive.numbers;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import edu.usc.projecttalent.cognitive.BaseActivity;
import edu.usc.projecttalent.cognitive.R;
import edu.usc.projecttalent.cognitive.databinding.ActivityNsExansBinding;

/**
 * Number section example 1 answer.
 *
 * @author Anindya Dutta
 * @version 2.0
 */

public class NSExAnswer extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NSExample example = (NSExample) getIntent().getExtras().get("example");
        ActivityNsExansBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_ns_exans);
        binding.setItem(example);

        LinearLayout series = (LinearLayout) findViewById(R.id.series);
        final EditText answer = (EditText) findViewById(R.id.answer);
        series.removeView(answer);
        series.addView(answer, example.getAnsPosition());

        (findViewById(R.id.next)).setOnClickListener(v -> {
            Intent intent;
            if (example.getId() == 1) {
                intent = new Intent(this, NSInstruction.class);
                intent.putExtra("second", true);
            } else {
                intent = new Intent(this, NSIntroduction.class);
            }
            startActivityForResult(intent, 1);
        });
    }
}

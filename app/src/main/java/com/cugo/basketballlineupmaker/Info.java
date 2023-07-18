package com.cugo.basketballlineupmaker;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import com.cugo.basketballlineupmaker.databinding.ActivityInfoBinding;

public class Info extends AppCompatActivity {
    private ActivityInfoBinding activityInfoBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        activityInfoBinding=ActivityInfoBinding.inflate(getLayoutInflater());
        View view  = activityInfoBinding.getRoot();
        super.onCreate(savedInstanceState);
        setContentView(view);
        activityInfoBinding.howtouse.setMovementMethod(new ScrollingMovementMethod());
    }
}
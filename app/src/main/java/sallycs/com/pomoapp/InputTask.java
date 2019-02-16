package sallycs.com.pomoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

//Creates the page where users input their task


public class InputTask extends AppCompatActivity {
    String name;
    String description;
    EditText n;
    EditText d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_task);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        n = findViewById(R.id.name);
        d = findViewById(R.id.description);

        Button create = findViewById(R.id.create_btn);

        /*when Create button us clicked on
         *  if name input is empty
         *      toast(warning) appears
         *  else
         *      if description length is greater than 0
         *          make a Task object using both description and name
         *      else
         *          make a Task object with only name
         */
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = n.getText().toString();
                description = d.getText().toString();
                if(name.length() == 0){
                    String warning = "The name needs to be filled out";
                    Toast note = Toast.makeText(view.getContext(), warning, Toast.LENGTH_SHORT);
                    note.show();
                }
                else {
                    String success = "Task Created!";
                    Toast note = Toast.makeText(view.getContext(), success, Toast.LENGTH_SHORT);


                    Task task;
                    if(description.length()>0) {
                        task = new Task(name, description);
                    }
                    else{
                        task = new Task(name);
                    }
                    Intent intent = new Intent();
                    intent.putExtra("TASK_KEY", task);
                    setResult(RESULT_OK, intent);
                    finish();

                    note.show();
                    InputTask.this.finish();

                }
            }
        });

    }


    public boolean onOptionsItemSelected(MenuItem item){
        int num = item.getItemId();
        if(num == android.R.id.home){
            this.finish();
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}

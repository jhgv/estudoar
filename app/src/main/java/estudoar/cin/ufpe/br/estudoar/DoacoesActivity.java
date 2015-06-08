package estudoar.cin.ufpe.br.estudoar;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class DoacoesActivity extends ListActivity {

    protected List<ParseObject> mDoacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> doacoes, com.parse.ParseException e) {
                if (e == null) {
                    mDoacoes = doacoes;
                    DoacaoAdapter adapter = new DoacaoAdapter(getListView().getContext(), mDoacoes);
                    setListAdapter(adapter);
                } else {
                    Log.d("doacao", "Error: " + e.getMessage());
                }
            }

        });

        setContentView(R.layout.activity_materiais);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_materiais, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

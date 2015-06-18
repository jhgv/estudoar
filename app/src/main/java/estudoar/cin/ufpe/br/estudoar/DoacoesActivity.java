package estudoar.cin.ufpe.br.estudoar;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class DoacoesActivity extends ListActivity {

    protected List<ParseObject> mDoacoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int filter = intent.getExtras().getInt("id_doador");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");

        if(filter == 1) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            query.whereEqualTo("doador", currentUser);
        }

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> doacoes, com.parse.ParseException e) {
                if (e == null) {
                    mDoacoes = doacoes;
                    DoacaoAdapter adapter = new DoacaoAdapter(getListView().getContext(), mDoacoes);
                    setListAdapter(adapter);

                    getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                                long arg3) {
                            ParseObject doacao = (ParseObject) mDoacoes.get(position);

                            Intent i = new Intent(DoacoesActivity.this, VerDoacaoActivity.class);

                            ParseUser doador = (ParseUser) doacao.get("doador");
                            i.putExtra("id_doacao", doacao.getObjectId());
                            i.putExtra("id_doador", doador.getObjectId());

                            startActivity(i);
                        }
                    });
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

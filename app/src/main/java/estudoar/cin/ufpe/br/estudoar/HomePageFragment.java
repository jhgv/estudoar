package estudoar.cin.ufpe.br.estudoar;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class HomePageFragment extends Fragment implements View.OnClickListener {

    private Button doarBtn;
    private Button procurarBtn;
    private Button primeiraDoacao;

    public HomePageFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View homeView = inflater.inflate(R.layout.fragment_home_page, container, false);

        if(savedInstanceState == null){
        }

        doarBtn = (Button) homeView.findViewById(R.id.btnDoar);
        doarBtn.setOnClickListener(this);

        procurarBtn = (Button) homeView.findViewById(R.id.btnProcurar);
        procurarBtn.setOnClickListener(this);

        primeiraDoacao = (Button) homeView.findViewById(R.id.btnPrimeiraDoacao);
        primeiraDoacao.setOnClickListener(this);

        return homeView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnDoar:
                goToDoarPage(v);
                break;
            case R.id.btnProcurar:
                goToProcurarPage(v);
                break;
            case R.id.btnPrimeiraDoacao:
                goToVerDoacaoPage(v);
                break;

        }
    }

/*
    public void redirectToLogin(){
        ParseUser.logOut();
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }
*/

    public void goToDoarPage(View view) {
        Intent i = new Intent(getActivity(), DoarActivity.class);
        startActivity(i);
    }

    public void goToProcurarPage(View view) {

    }

    public void goToVerDoacaoPage(View view){

        ParseUser currentUser = ParseUser.getCurrentUser();

        final Intent i = new Intent(getActivity(), VerDoacaoActivity.class);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
        query.whereEqualTo("doador", currentUser);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject doacao, ParseException e) {
                if (e == null) {
                    ParseUser doador = (ParseUser) doacao.get("doador");
                    i.putExtra("id_doacao", doacao.getObjectId());
                    i.putExtra("id_doador", doador.getObjectId());
                    startActivity(i);
                } else {
                    Toast.makeText(getActivity(), "Você ainda não fez uma doação!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}

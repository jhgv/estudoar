package estudoar.cin.ufpe.br.estudoar;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class ContatoDoadorFragment extends Fragment{

    private TextView nome_doador;
    private TextView email_doador;

    public ContatoDoadorFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        if(savedInstanceState == null){
        }

        final View contatoDoadorView = inflater.inflate(R.layout.fragment_contato_doador,container,false);

        Intent intent = getActivity().getIntent();
        String id_doador = intent.getExtras().getString("id_doador");

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", id_doador);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser usuario, ParseException e) {
                if (e == null) {
                    nome_doador = (TextView) contatoDoadorView.findViewById(R.id.nome_doador);
                    email_doador = (TextView) contatoDoadorView.findViewById(R.id.email_doador);

                    nome_doador.setText(usuario.getString("name"));
                    email_doador.setText(usuario.getEmail());

                } else {
                    Toast.makeText(getActivity(), "Erro ao tentar acessar o doador", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return contatoDoadorView;
    }
/*
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

*/
}

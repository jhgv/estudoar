package estudoar.cin.ufpe.br.estudoar;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

        View contatoDoadorView = inflater.inflate(R.layout.fragment_contato_doador,container,false);

        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser != null){
            nome_doador = (TextView) contatoDoadorView.findViewById(R.id.nome_doador);
            email_doador = (TextView) contatoDoadorView.findViewById(R.id.email_doador);

            nome_doador.setText(currentUser.getString("name"));
            email_doador.setText(currentUser.getEmail());

        }else{
            // Voltar pra tela de login?
        }

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

package estudoar.cin.ufpe.br.estudoar;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

public class MeuPerfilFragment extends Fragment implements View.OnClickListener {

    private Button editarBtn;
    private Button usuarioDoacoesBtn;

    private String id_usuario;

    private TextView nome;
    private TextView email;

    private boolean meu_perfil = false;

    public MeuPerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View meuPerfilView = inflater.inflate(R.layout.fragment_meu_perfil, container, false);

        if(savedInstanceState == null){
        }

        Intent intent = getActivity().getIntent();
        id_usuario = intent.getExtras().getString("id_usuario");

        nome = (TextView) meuPerfilView.findViewById(R.id.nome_usuario);
        email = (TextView) meuPerfilView.findViewById(R.id.email_usuario);

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("objectId", id_usuario);
        query.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser usuario, ParseException e) {
                if (e == null) {
                    nome.setText(usuario.getString("name"));
                    email.setText(usuario.getEmail());

                } else {
                    Toast.makeText(getActivity(), "Erro ao tentar acessar o doador", Toast.LENGTH_SHORT).show();
                }
            }
        });

        String current_user = ParseUser.getCurrentUser().getObjectId();

        editarBtn = (Button) meuPerfilView.findViewById(R.id.btnEditarPerfil);
        usuarioDoacoesBtn = (Button) meuPerfilView.findViewById(R.id.btnMinhasDoacoes);

        if(current_user.equals(id_usuario)){
            meu_perfil = true;
            editarBtn.setOnClickListener(this);
        }else{
            editarBtn.setVisibility(View.GONE);
        }

        usuarioDoacoesBtn.setOnClickListener(this);

        return meuPerfilView;
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
            case R.id.btnMinhasDoacoes:
                goToMinhasDoacoesPage(v);
                break;
            case R.id.btnEditarPerfil:
                goToEditarPerfilPage(v);
                break;

        }
    }

    public void goToMinhasDoacoesPage(View v) {
        Intent i = new Intent(getActivity(), DoacoesActivity.class);
        if(meu_perfil){
            i.putExtra("filter",1);
        }else{
            i.putExtra("filter",4);
            i.putExtra("id_usuario",id_usuario);
        }
        startActivity(i);
    }

    public void goToEditarPerfilPage(View v) {
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_meu_perfil, new EditarPerfilFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }


}

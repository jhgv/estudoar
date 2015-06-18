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

import com.parse.ParseUser;

public class MeuPerfilFragment extends Fragment implements View.OnClickListener {

    private Button editarBtn;
    private Button minhasDoacoesBtn;

    private TextView nome;
    private TextView email;


    public MeuPerfilFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View meuPerfilView = inflater.inflate(R.layout.fragment_meu_perfil, container, false);

        if(savedInstanceState == null){
        }

        ParseUser currentUser = ParseUser.getCurrentUser();

        nome = (TextView) meuPerfilView.findViewById(R.id.nome_usuario);
        email = (TextView) meuPerfilView.findViewById(R.id.email_usuario);

        editarBtn = (Button) meuPerfilView.findViewById(R.id.btnEditarPerfil);
        minhasDoacoesBtn = (Button) meuPerfilView.findViewById(R.id.btnMinhasDoacoes);

        nome.setText(currentUser.getString("name"));
        email.setText(currentUser.getEmail());

        editarBtn.setOnClickListener(this);
        minhasDoacoesBtn.setOnClickListener(this);

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
        i.putExtra("id_doador",1);
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

package estudoar.cin.ufpe.br.estudoar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class DoarFragment extends Fragment implements View.OnClickListener {

    public static final int GET_FROM_GALLERY = 3;

    private EditText nomeImput;
    private EditText assuntoImput;
    private EditText descricaoImput;

    private Button fotoBtn;
    private Button doarBtn;

    private Spinner categorias_spinner;
    private String categoriaSelecionada = "Livro";

    private ImageView fotoView;

    public DoarFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View doarView = inflater.inflate(R.layout.fragment_doar,container,false);

        if(savedInstanceState == null){
        }

        nomeImput = (EditText) doarView.findViewById(R.id.nome_doar);
        assuntoImput = (EditText) doarView.findViewById(R.id.assunto_doar);
        descricaoImput = (EditText) doarView.findViewById(R.id.descricao_doar);

        fotoBtn = (Button) doarView.findViewById(R.id.btnFoto);
        doarBtn = (Button) doarView.findViewById(R.id.btnDoar);

        fotoBtn.setOnClickListener(this);
        doarBtn.setOnClickListener(this);

        fotoView = (ImageView) doarView.findViewById(R.id.fotoView);

        categorias_spinner = (Spinner) doarView.findViewById(R.id.spinner_doar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(),
                                        R.array.categorias_array,android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorias_spinner.setAdapter(adapter);

        categorias_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView parentView, View selectedItemView, int position, long id) {
                categoriaSelecionada = (String) categorias_spinner.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView parentView) {

            }
        });

        return doarView;
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
                doarMaterial(v);
                break;

            case R.id.btnFoto:
                uploadFoto(v);
                break;

        }
    }

    public void doarMaterial(View view ){

        ParseUser currentUser = ParseUser.getCurrentUser();

        if(currentUser == null){
            Toast.makeText(getActivity(), "Usuario nao logado", Toast.LENGTH_LONG).show();
        }else {

            String nome = nomeImput.getText().toString().trim();
            String assunto = assuntoImput.getText().toString().trim();
            String descricao = descricaoImput.getText().toString().trim();

            Bitmap foto = ((BitmapDrawable)fotoView.getDrawable()).getBitmap();

            ParseObject doacao = new ParseObject("Doacao");

            doacao.put("nome", nome);
            doacao.put("categoria", categoriaSelecionada);
            doacao.put("assunto", assunto);
            doacao.put("descricao", descricao);

            if (foto == null){
                doacao.put("foto","");
            }else{
                // Convert it to byte
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                // Compress image to lower quality scale 1 - 100
                foto.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                byte[] image = stream.toByteArray();

                // Create the ParseFile
                ParseFile file = new ParseFile(nome  + ".jpg", image);
                // Upload the image into Parse Cloud
                file.saveInBackground();

                doacao.put("foto", file);
            }

            doacao.put("doador", currentUser);

            doacao.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Toast.makeText(getActivity(), "Doacao cadastrada", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                    } else {
                        Toast.makeText(getActivity(), "Erro ao cadastrar", Toast.LENGTH_LONG).show();
                    }
                }
            });

        }
    }

    public void uploadFoto(View view){
        startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Detects request codes
        if(requestCode==GET_FROM_GALLERY && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = data.getData();
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                fotoView.setImageBitmap(bitmap);

            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

}

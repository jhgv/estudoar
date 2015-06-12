package estudoar.cin.ufpe.br.estudoar;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class EditarDoacaoFragment extends Fragment implements View.OnClickListener{

    public static final int GET_FROM_GALLERY = 3;

    private EditText nomeImput;
    private EditText assuntoImput;
    private EditText descricaoImput;

    private Button fotoBtn;
    private Button editarDoacaoBtn;

    private ParseUser currentUser;

    private Spinner categorias_spinner;
    private String categoriaSelecionada;

    private ImageView fotoView;

    private ParseObject doacao_editada;

    public EditarDoacaoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        if(savedInstanceState == null){
        }

        View editarDoacaoView = inflater.inflate(R.layout.fragment_editar_doacao,container,false);

        currentUser = ParseUser.getCurrentUser();

        nomeImput = (EditText) editarDoacaoView.findViewById(R.id.nome_doacao_editar);
        assuntoImput = (EditText) editarDoacaoView.findViewById(R.id.assunto_doacao_editar);
        descricaoImput = (EditText) editarDoacaoView.findViewById(R.id.descricao_doacao_editar);

        fotoView = (ImageView) editarDoacaoView.findViewById(R.id.fotoViewEditar);

        categorias_spinner = (Spinner) editarDoacaoView.findViewById(R.id.spinner_doacao_editar);

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

        fotoBtn = (Button) editarDoacaoView.findViewById(R.id.btnFotoEditar);

        editarDoacaoBtn = (Button) editarDoacaoView.findViewById(R.id.btnEditarDoacao);

        Intent intent = getActivity().getIntent();

        String id_doacao = intent.getExtras().getString("id_doacao");

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
        query.getInBackground(id_doacao, new GetCallback<ParseObject>() {
            public void done(ParseObject doacao, ParseException e) {
                if (e == null) {
                    doacao_editada = doacao;

                    nomeImput.setText(doacao.getString("nome"));

                    String categoria_antiga = doacao.getString("categoria");
                    ArrayAdapter myAdap = (ArrayAdapter) categorias_spinner.getAdapter();
                    int spinnerPosition = myAdap.getPosition(categoria_antiga);
                    categorias_spinner.setSelection(spinnerPosition);

                    assuntoImput.setText(doacao.getString("assunto"));
                    descricaoImput.setText(doacao.getString("descricao"));

                    final ParseFile image_file = (ParseFile) doacao.getParseFile("foto");

                    new Thread(new Runnable() {
                        public void run() {
                            fotoView.post(new Runnable() {
                                public void run() {
                                    if (image_file != null) {
                                        image_file.getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] data, ParseException e) {
                                                if (e == null) {
                                                    Bitmap bmp = BitmapFactory.decodeByteArray(data, 0,data.length);

                                                    if (bmp != null) fotoView.setImageBitmap(bmp);
                                                }
                                            }
                                        });
                                    } else
                                        fotoView.setPadding(10, 10, 10, 10);
                                }
                            });
                        }
                    }).start();

                } else Toast.makeText(getActivity(), "ID Desconhecido!", Toast.LENGTH_LONG).show();
            }
        });

        editarDoacaoBtn.setOnClickListener(this);
        fotoBtn.setOnClickListener(this);

        return editarDoacaoView;
    }

    @Override
    public void onClick(View v) {
        int key = v.getId();

        switch (key) {
            case R.id.btnEditarDoacao:
                salvarMudancas(v);
                break;
            case R.id.btnFotoEditar:
                uploadFoto(v);
                break;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    public void goToContatoDoador(View view){
        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_ver_doacao, new ContatoDoadorFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void salvarMudancas(View view){
        String nome = nomeImput.getText().toString().trim();
        String assunto = assuntoImput.getText().toString().trim();
        String descricao = descricaoImput.getText().toString().trim();

        Bitmap foto = ((BitmapDrawable)fotoView.getDrawable()).getBitmap();

        doacao_editada.put("nome", nome);
        doacao_editada.put("categoria", categoriaSelecionada);
        doacao_editada.put("assunto", assunto);
        doacao_editada.put("descricao", descricao);

        if (foto == null){
            doacao_editada.put("foto",null);
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

            doacao_editada.put("foto", file);
        }

        doacao_editada.put("doador", currentUser);

        doacao_editada.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(getActivity(), "Doacao modificada", Toast.LENGTH_LONG).show();
                    goToVerDoacao();
                } else {
                    Toast.makeText(getActivity(), "Erro ao salvar alterações", Toast.LENGTH_LONG).show();
                }
            }
        });

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

    public void goToVerDoacao(){
        Intent i = new Intent(getActivity(), VerDoacaoActivity.class);
        i.putExtra("id_doacao", doacao_editada.getObjectId());
        i.putExtra("id_doador", currentUser.getObjectId());
        getActivity().setIntent(i);

        FragmentManager fm = getFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
        transaction.replace(R.id.fragment_ver_doacao, new VerDoacaoFragment());
        transaction.addToBackStack(null);
        transaction.commit();
    }

}

package estudoar.cin.ufpe.br.estudoar;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.squareup.picasso.Picasso;

import java.util.List;

public class InteressadosAdapter extends ArrayAdapter {
    protected Context context;
    protected List favoritos;

    public InteressadosAdapter(Context context, List favoritos) {
        super(context, R.layout.materiais_custom, favoritos);
        this.context = context;
        this.favoritos = favoritos;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.materiais_custom, null);

            holder = new ViewHolder();
            holder.nomeDoador = (TextView) convertView.findViewById(R.id.materialName);
            holder.nomeDoacao = (TextView) convertView.findViewById(R.id.materialDesc);
            holder.imagemDoacao = (ImageView) convertView.findViewById(R.id.materialImage);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();

        }

        ParseObject favoritoObject = (ParseObject) favoritos.get(position);

        ParseQuery<ParseUser> queryUser = ParseUser.getQuery();
        queryUser.whereEqualTo("objectId", (String) favoritoObject.get("interessado"));
        queryUser.getFirstInBackground(new GetCallback<ParseUser>() {
            public void done(ParseUser interessado, ParseException e) {
                if (e == null) {
                    holder.nomeDoador.setText(interessado.getUsername());
                } else {
                    Toast.makeText(context, "Erro ao recuperar usuário interessado", Toast.LENGTH_SHORT).show();
                }
            }
        });

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Doacao");
        query.whereEqualTo("objectId", (String) favoritoObject.get("doacao"));
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject doacao, ParseException e) {
                if (e == null) {
                    holder.nomeDoacao.setText((String) doacao.get("nome"));
                    ParseFile foto = (ParseFile) doacao.get("foto");
                    Log.d("foto", foto.getName() + " " + foto.getUrl());
                    Picasso.with(context)
                            .load(foto.getUrl())
                            .resize(200, 200).centerCrop()
                            .into(holder.imagemDoacao);
                } else {
                    Toast.makeText(context, "Erro ao recuperar doação", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return convertView;
    }

    public static class ViewHolder {
        TextView nomeDoador;
        TextView nomeDoacao;
        ImageView imagemDoacao;
    }

}

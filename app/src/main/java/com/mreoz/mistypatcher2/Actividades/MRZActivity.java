package com.mreoz.mistypatcher2.Actividades;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.ipc.VActivityManager;
import com.mreoz.mistypatcher2.AcountSettings;
import com.mreoz.mistypatcher2.Preferencias.Prefs;
import com.mreoz.mistypatcher2.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import static com.mreoz.mistypatcher2.Adaptadores.MRZH4CK3R.getSrv;
import static com.mreoz.mistypatcher2.Adaptadores.MRZH4CK3R.getdir;
import static com.mreoz.mistypatcher2.Adaptadores.MRZH4CK3R.getdwcdir;
import static com.mreoz.mistypatcher2.Adaptadores.MRZH4CK3R.getdwdir;
import static com.mreoz.mistypatcher2.Adaptadores.MRZH4CK3R.getfirma1;
import static com.mreoz.mistypatcher2.Adaptadores.MRZH4CK3R.nn;


public class MRZActivity extends AppCompatActivity {
    static String GVER = "GVER";

    static String ver = "7.1.5";

    private static final String TAG = MRZActivity.class.getSimpleName();

    private String GAVAIL = "GAVAIL";

    private String allver = ver;

    String name = "";

    private AlertDialog.Builder builder;

    private int exec = 0;

    private ProgressDialog pd;

    private String pkg = "block.app.wars";
    final String appname = "Block City Wars";
    public static String pkg2 = "block.app.wars";

    private void copyFile(InputStream paramInputStream, String paramString) throws IOException {
        byte[] arrayOfByte = new byte[2048];
        FileOutputStream fileOutputStream = new FileOutputStream(paramString);
        while (true) {
            int i = paramInputStream.read(arrayOfByte);
            if (i != -1) {
                fileOutputStream.write(arrayOfByte, 0, i);
                continue;
            }
            break;
        }
        paramInputStream.close();
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    private void updateUserStatus(String state)
    {
        DatabaseReference UsersRef;
        FirebaseAuth auth = null;
        String currentUserId = auth.getCurrentUser().getUid();
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        String saveCurrentTime, saveCurrentDate;

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());

        HashMap<String, Object> onlineStateMap = new HashMap<>();
        onlineStateMap.put("time", saveCurrentTime);
        onlineStateMap.put("date", saveCurrentDate);
        onlineStateMap.put("state", state);

        UsersRef.child(currentUserId).child("userState")
                .updateChildren(onlineStateMap);
    }

    private void createShortcut() {
        try {
            createShortcut(this.pkg);
            Toast.makeText(this, "Game Shortcut Created on Launcher :)", Toast.LENGTH_LONG).show();
            return;
        } catch (Exception exception) {
            exception.printStackTrace();
            return;
        }
    }

    private String getVersion() {
        PackageManager packageManager = getPackageManager();
        try {
            return (packageManager.getPackageInfo(this.pkg, 0)).versionName;
        } catch (android.content.pm.PackageManager.NameNotFoundException ackageManager) {
            return "N/A";
        }
    }

    private boolean isGameInstalled() { return (getPackageManager().getLaunchIntentForPackage(this.pkg) != null); }

    private boolean isInternetAvailable() {
        ConnectivityManager cnnectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cnnectivityManager != null) {
            NetworkInfo networkInfo = cnnectivityManager.getActiveNetworkInfo();
        } else {
            cnnectivityManager = null;
        }
        return (cnnectivityManager != null && cnnectivityManager.getActiveNetworkInfo().isConnected());
    }

    private boolean isServiceRunning() {
        ActivityManager activityManager = (ActivityManager)getSystemService(Context.ACTIVITY_SERVICE);
        if (activityManager != null)
            for (ActivityManager.RunningServiceInfo runningServiceInfo : activityManager.getRunningServices(2147483647)) {
                if (Floater.class.getName().equals(runningServiceInfo.service.getClassName()))
                    return true;
            }
        return false;
    }

    private void startFloater() {
        if (!isServiceRunning())
            startService(new Intent(this, Floater.class));
    }

    private void startGame() {
        launchApp(this.pkg);
        (new Handler()).postDelayed(new Runnable() {
            public void run() { Toast.makeText(MRZActivity.this, "!!Modded By MrEoZ!!", Toast.LENGTH_LONG).show(); }
        },1300L);
    }

    private void startPatcher() {
        if (Build.VERSION.SDK_INT >= 23 && !Settings.canDrawOverlays(this)) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("package:");
            stringBuilder.append(getPackageName());
            startActivityForResult(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION", Uri.parse(stringBuilder.toString())), 123);
            return;
        }
        startFloater();
    }

    public void createShortcut(String paramString) {
        VirtualCore.OnEmitShortcutListener onEmitShortcutListener = new VirtualCore.OnEmitShortcutListener() {
            public Bitmap getIcon(Bitmap param1Bitmap) { return param1Bitmap; }

            public String getName(String param1String) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(param1String);
                stringBuilder.append("(MRZ)");
                return stringBuilder.toString();
            }
        };
        VirtualCore.get().createShortcut(0, paramString, onEmitShortcutListener);
    }

    boolean getB(String paramString) { return Prefs.with(this).readBoolean(paramString, false); }

    String getS(String paramString1, String paramString2) { return Prefs.with(this).read(paramString1, paramString2); }

    public void installApp(String paramString) {
        try {
            VirtualCore.get().installPackage((getPackageManager().getApplicationInfo(paramString, 0)).sourceDir, 36);
            return;
        } catch (android.content.pm.PackageManager.NameNotFoundException paraString) {
            paraString.printStackTrace();
            return;
        }
    }

    public void launchApp(String paramString) { VActivityManager.get().startActivity(VirtualCore.get().getLaunchIntent(paramString, 0), 0); }

    @SuppressLint("MissingSuperCall")
    protected void onActivityResult(int paramInt1, int paramInt2, Intent paramIntent) {
        if (paramInt1 == 123 && Build.VERSION.SDK_INT >= 23) {
            if (Settings.canDrawOverlays(this)) {
                startFloater();
                return;
            }
            Toast.makeText(this, "Please allow this permission, so MisTyPatcher could be drawn.", Toast.LENGTH_LONG).show();
        }
    }

    public void onCheck() {
        final ProgressDialog mancito = new ProgressDialog(MRZActivity.this);
        mancito.setTitle("Checking");
        mancito.setMessage("Checking Things...");
        mancito.setIndeterminate(true);
        mancito.setCancelable(false);
        mancito.setCanceledOnTouchOutside(false);
        mancito.show();
        (new Thread(new Runnable() {
        int flag = 1;
        public void run() {
            try {
                MRZActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (!MRZActivity.this.isGameInstalled()) {
                            AlertDialog.Builder maincra = new AlertDialog.Builder(MRZActivity.this);
                            maincra.setMessage("Please install the game "+appname+" to continue with this app.");
                            maincra.setTitle("Game Not Found!!!").setPositiveButton("Download It!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface param3DialogInterface, int param3Int) {
                                    MRZActivity mrzActivity = MRZActivity.this;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("https://play.google.com/store/apps/details?id=");
                                    stringBuilder.append(MRZActivity.this.pkg);
                                    mrzActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
                                }
                            }).setNeutralButton("Later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface param3DialogInterface, int param3Int) { MRZActivity.this.finish(); }
                            }).setCancelable(false).create().show();
                            flag = 0;
                            mancito.dismiss();
                            return;
                        }
                        int fname = Integer.parseInt((MRZActivity.this.getVersion().replace(".", "")));
                        int mrz = Integer.parseInt(ver.replace(".", ""));
                        if (MRZActivity.this.isGameInstalled() && !MRZActivity.this.getB(MRZActivity.this.GAVAIL) && (fname < mrz)) {
                            AlertDialog.Builder lolcito = new AlertDialog.Builder(MRZActivity.this);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("It seems that you are using an Other version of the "+appname+" v");
                            stringBuilder.append(MRZActivity.this.getVersion());
                            stringBuilder.append(". \nSo Please Use + v");
                            stringBuilder.append(MRZActivity.this.allver);
                            stringBuilder.append(" to continue.");
                            lolcito.setMessage(stringBuilder.toString());
                            lolcito.setTitle("Attention needed!!!").setPositiveButton("Download It!", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface param3DialogInterface, int param3Int) {
                                    MRZActivity aActivity = MRZActivity.this;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    stringBuilder.append("https://play.google.com/store/apps/details?id=");
                                    stringBuilder.append(MRZActivity.this.pkg);
                                    aActivity.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(stringBuilder.toString())));
                                }
                            }).setNeutralButton("Later", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface param3DialogInterface, int param3Int) {
                                    flag = 0;
                                    mancito.dismiss();
                                    finish();
                                }
                            }).setCancelable(false).create().show();
                            flag = 0;
                            mancito.dismiss();
                        }
                        if (MRZActivity.this.isGameInstalled() && !MRZActivity.this.getB(MRZActivity.this.GAVAIL) && (fname > mrz)) {
                            AlertDialog.Builder lolcito = new AlertDialog.Builder(MRZActivity.this);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("You have a fairly recent version of "+appname+", and our database is not adapted for this version v");
                            stringBuilder.append(MRZActivity.this.getVersion());
                            stringBuilder.append(". \nOur database is adapted to version v");
                            stringBuilder.append(MRZActivity.this.allver);
                            stringBuilder.append(". \nWait for 24 to 48 hours until our database is updated, sorry and happy hacking.");
                            lolcito.setMessage(stringBuilder.toString());
                            lolcito.setTitle("Attention needed!!!").setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface param3DialogInterface, int param3Int) {
                                    flag = 0;
                                    mancito.dismiss();
                                    finish();
                                }
                            }).setCancelable(false).create().show();
                            flag = 0;
                            mancito.dismiss();
                        }
                    }
                });
                Thread.sleep(700L);
                if (VirtualCore.get().getInstalledAppInfo(pkg, 0) != null)
                    mancito.dismiss();
                if (this.flag == 1) {
                    MRZActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            mancito.setTitle("Starting");
                            mancito.setMessage("Please wait...");
                        }
                    });
                    MRZActivity.this.setS(MRZActivity.GVER, MRZActivity.this.getVersion());
                    MRZActivity.this.installApp(MRZActivity.this.pkg);
                    Thread.sleep(700L);
                }
                MRZActivity.this.runOnUiThread(new Runnable() {
                    public void run() { mancito.dismiss(); }
                });
                return;
            } catch (Exception exception) {
                exception.printStackTrace();
                MRZActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        mancito.dismiss();
                    }
                });
                return;
            }
        }
        })).start(); }

    @SuppressLint("ResourceType")
    private void Loader() {
        if (!new File(getFilesDir()+getdwdir()).exists()){
            try {
                MRZActivity.this.setB(MRZActivity.this.GAVAIL, true);
                String fname = MRZActivity.this.getVersion().replace(".", "");
                new DownloadFile().execute(getSrv() +MRZActivity.pkg2+ File.separator +fname+ nn());

            } catch (Exception e) {
                e.printStackTrace();
                MRZActivity.this.setB(MRZActivity.this.GAVAIL, false);
                onCheck();
            }
        } else{
            onCheck();
        }
     }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    public void checkConnection(){
        if(isOnline()){
            Update();
        }else{
            AlertDialog.Builder lolcito = new AlertDialog.Builder(MRZActivity.this);
            StringBuilder stringBuilde = new StringBuilder();
            stringBuilde.append("Internet no disponible");
            lolcito.setMessage(stringBuilde.toString());
            lolcito.setTitle("Attention needed!!!").setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface param3DialogInterface, int param3Int) {
                    finish();
                }
            }).setCancelable(false).create().show();
        }
    }

    private void Update() {
        Loader();
    }

    protected void onCreate(Bundle paramBundle) {
        super.onCreate(paramBundle);
        checkConnection();
        try {
            for (Signature signature : (getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES)).signatures) {
                MessageDigest messageDigest = MessageDigest.getInstance("SHA");
                messageDigest.update(signature.toByteArray());
                if (!getfirma1().equals(Base64.encodeToString(messageDigest.digest(), 0).trim())){
                    AlertDialog.Builder lolcito = new AlertDialog.Builder(MRZActivity.this);
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Please download original patcher");
                    lolcito.setMessage(stringBuilder.toString());
                    lolcito.setTitle("App not original!!").setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface param3DialogInterface, int param3Int) {
                            finish();
                        }
                    }).setCancelable(false).create().show();
                }
            }
        } catch (Exception aramBundle) {
            aramBundle.printStackTrace();
        }
        setContentView(R.layout.activity_main);
        LinearLayout button1 = findViewById(R.id.bt1);
        LinearLayout button2 = findViewById(R.id.bt2);
        LinearLayout button3 = findViewById(R.id.bt3);
        LinearLayout button4 = findViewById(R.id.bt4);
        Button button5 = findViewById(R.id.admconsole);
        Button button6 = findViewById(R.id.imageView3);
        final Button mods = findViewById(R.id.mods);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        button5.setVisibility(View.GONE);
        findViewById(R.id.admp).setVisibility(View.GONE);
        TextView asd = findViewById(R.id.tv);
        asd.setText("Tipo de cuenta: USUARIO - Cualquier tipo de infiltracion de esta cuenta sera penalizada con un baneo de IMEI a todos los dispositivos que la usen - Sigan mi canal de YT para tener las ultimas noticias sobre este patcher \"Youtube.com/MrEoZ\"");
        try {
            if (!(new File(Environment.getExternalStorageDirectory(), "/MrEoZ/")).exists())
                (new File(Environment.getExternalStorageDirectory(), "/MrEoZ/")).mkdir();
            if (!(new File(Environment.getExternalStorageDirectory(), "/MrEoZ/logs/")).exists())
                (new File(Environment.getExternalStorageDirectory(), "/MrEoZ/logs/")).mkdir();
            Runtime runtime = Runtime.getRuntime();
            StringBuilder tringBuilder = new StringBuilder();
            tringBuilder.append("logcat -f ");
            tringBuilder.append(Environment.getExternalStorageDirectory().getAbsolutePath());
            tringBuilder.append("/mreoz/logs/MisTyPatcher.txt");
            runtime.exec(tringBuilder.toString());
        } catch (IOException iOException) {
            iOException.printStackTrace();
            Toast.makeText(this, "Logs Fail", Toast.LENGTH_LONG).show();
        }
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                MRZActivity.this.startPatcher();
                Snackbar.make(param1View, "Launching Patcher", Snackbar.LENGTH_SHORT).show();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                MRZActivity.this.startGame();
                Snackbar.make(param1View, "Launching Game", Snackbar.LENGTH_LONG).show();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                MRZActivity.this.createShortcut();
                Snackbar.make(param1View, "Creating Shortcut", Snackbar.LENGTH_SHORT).show();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View param1View) {
                VirtualCore.get().killAllApps();
                MRZActivity.this.uninstallApp(pkg);
                onCheck();
                Snackbar.make(param1View, "Stopping game", Snackbar.LENGTH_LONG).show();
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(MRZActivity.this, AcountSettings.class);
                startActivity(mainIntent);
            }
        });
        mods.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mods.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.botoes,null));
                mods.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(MRZActivity.this,"Solo puedes actualizar los mods una vez por boot",Toast.LENGTH_LONG).show();
                    }
                });
                try {
                    MRZActivity.this.setB(MRZActivity.this.GAVAIL, true);
                    String fname = MRZActivity.this.getVersion().replace(".", "");
                    new DownloadFile().execute(getSrv() +MRZActivity.pkg2+ File.separator +fname+ nn());

                } catch (Exception e) {
                    e.printStackTrace();
                    MRZActivity.this.setB(MRZActivity.this.GAVAIL, false);
                    onCheck();
                }
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    protected void onPause() {
        super.onPause();
    }

    protected void onRestart() {
        super.onRestart();
        if ((!isGameInstalled() && VirtualCore.get().getInstalledAppInfo(this.pkg, 0) != null)){
            onCheck();
        }
    }

    protected void onResume() {
        super.onResume();
    }

    void setB(String paramString, boolean paramBoolean) { Prefs.with(this).writeBoolean(paramString, paramBoolean); }

    void setS(String paramString1, String paramString2) { Prefs.with(this).write(paramString1, paramString2); }

    public void uninstallApp(String paramString) { VirtualCore.get().uninstallPackage(paramString); }


    private class DownloadFile extends AsyncTask<String, String, String> {
        private ProgressDialog progressDialog;
        private String fileName;
        private String fname = null;
        private String folder;


        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            new File(getFilesDir() + getdwdir()).delete();
            new File(getFilesDir() + getdwcdir()).delete();
            this.progressDialog = new ProgressDialog(MRZActivity.this);
            this.progressDialog.setTitle("Downloading Mods");
            this.progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            this.progressDialog.setCancelable(false);
            this.progressDialog.show();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            fname = MRZActivity.this.getVersion().replace(".", "");
            int count;
            try {
                URL url = new URL(f_url[0]);
                URLConnection connection =  url.openConnection();
                connection.connect();
                // getting file length
                int lengthOfFile = connection.getContentLength();


                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String timestamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());

                //Extract file name from URL
                fileName = f_url[0].substring(f_url[0].lastIndexOf('/') + 1, f_url[0].length());

                //Append timestamp to file name
                fileName = timestamp + "_" + fileName;

                //External directory path to save file
                folder = Environment.getExternalStorageDirectory() + File.separator + "mreoz/mods/";
                if(!new File(getFilesDir()+getdir()).exists()){
                    new File(getFilesDir()+getdir()).mkdir();
                }

                // Output stream to write file
                OutputStream output = new FileOutputStream(getFilesDir()+getdwdir());

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress("" + (int) ((total * 100) / lengthOfFile));
                    Log.d(TAG, "Progress: " + (int) ((total * 100) / lengthOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return "Downloaded at: " + folder + fileName;

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
                MRZActivity.this.setB(MRZActivity.this.GAVAIL, false);
            }

            return "Something went wrong";
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            progressDialog.setProgress(Integer.parseInt(progress[0]));
        }


        @Override
        protected void onPostExecute(String message) {
            // dismiss the dialog after the file was downloaded
            this.progressDialog.dismiss();
            onCheck();
        }
    }
}

package org;

@Bean("sslContextParameters")
public SSLContextParameters createSslContextParameters() {
    SSLContextParameters sslContextParameters = new SSLContextParameters();
    
    TrustManagersParameters trustManagersParameters = new TrustManagersParameters();
    trustManagersParameters.setTrustManager(new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType) {}
        public void checkServerTrusted(X509Certificate[] chain, String authType) {}
        public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
    });
    sslContextParameters.setTrustManagers(trustManagersParameters);
    
    return sslContextParameters;
}
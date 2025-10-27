const axios = require('axios');

async function testCorrectedFlow() {
    console.log('🔧 Test du flux corrigé\n');

    const AUTH_URL = 'http://localhost:8081/v1/api/auth';
    const USER_URL = 'http://localhost:8083/v2/user';
    
    try {
        // 1. Connexion Admin
        console.log('1️⃣ Connexion Admin...');
        const adminLogin = await axios.post(`${AUTH_URL}/signin`, {
            username: 'admin',
            password: 'admin123'
        });
        console.log('✅ Admin connecté');

        // 2. Création utilisateur
        console.log('\n2️⃣ Création utilisateur...');
        const timestamp = Date.now().toString().slice(-6);
        const newUser = {
            username: `test${timestamp}`,
            name: 'Test User',
            email: `test${timestamp}@test.com`,
            password: 'test123',
            role: 'EMPLOYE',
            department: 'DIRECTION_TECHNIQUE',
            entreprise: 'ICCSOFT',
            fonction: 'INGENIEUR_DEVELOPPEUR'
        };

        const createResponse = await axios.post(USER_URL, newUser);
        console.log('✅ Utilisateur créé:', createResponse.data.username);

        // 3. Vérification endpoint username
        console.log('\n3️⃣ Test endpoint username...');
        const userByUsername = await axios.get(`${USER_URL}/username/${newUser.username}`);
        console.log('✅ Utilisateur trouvé par username:', userByUsername.data.username);

        // 4. Test connexion
        console.log('\n4️⃣ Test connexion utilisateur...');
        const userLogin = await axios.post(`${AUTH_URL}/signin`, {
            username: newUser.username,
            password: 'test123'
        });

        if (userLogin.data.accessToken) {
            console.log('✅ Connexion réussie!');
            
            const profile = await axios.get(`${AUTH_URL}/me`, {
                headers: { Authorization: `Bearer ${userLogin.data.accessToken}` }
            });
            
            console.log('✅ Profil:', profile.data.username, '-', profile.data.role);
        }

        // 5. Nettoyage
        await axios.delete(`${USER_URL}/${createResponse.data.id}`);
        console.log('\n🧹 Nettoyage effectué');
        
        console.log('\n🎉 FLUX CORRIGÉ FONCTIONNE!');

    } catch (error) {
        console.error('❌ Erreur:', error.response?.data || error.message);
    }
}

testCorrectedFlow();
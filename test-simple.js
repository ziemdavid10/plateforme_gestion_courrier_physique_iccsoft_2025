const http = require('http');

// Test simple de connectivité sans dépendances externes
async function testServices() {
    console.log('🧪 Test d\'intégration Frontend-Backend\n');
    
    const services = [
        { name: 'Service Auth', port: 8081, path: '/v1/api/auth/signin' },
        { name: 'Service Courrier', port: 8082, path: '/v3/courrier' },
        { name: 'Service User', port: 8083, path: '/v2/user/all' }
    ];
    
    console.log('🔗 Test de connectivité des services...\n');
    
    for (const service of services) {
        try {
            const result = await testService(service.port, service.path);
            if (result.connected) {
                console.log(`✅ ${service.name} (port ${service.port}): ACCESSIBLE`);
                console.log(`   Status: ${result.statusCode}`);
            } else {
                console.log(`❌ ${service.name} (port ${service.port}): NON ACCESSIBLE`);
                console.log(`   Erreur: ${result.error}`);
            }
        } catch (error) {
            console.log(`❌ ${service.name} (port ${service.port}): ERREUR`);
            console.log(`   ${error.message}`);
        }
        console.log('');
    }
    
    console.log('📋 Instructions pour les tests complets:');
    console.log('1. Assurez-vous que tous les services backend sont démarrés');
    console.log('2. Ouvrez test-frontend-backend.html dans votre navigateur');
    console.log('3. Cliquez sur "Exécuter Tous les Tests"');
    console.log('\n🌐 Ou testez manuellement:');
    console.log('- Frontend: http://localhost:4200');
    console.log('- Auth API: http://localhost:8081/v1/api/auth');
    console.log('- Courrier API: http://localhost:8082/v3/courrier');
    console.log('- User API: http://localhost:8083/v2/user/all');
}

function testService(port, path) {
    return new Promise((resolve) => {
        const options = {
            hostname: 'localhost',
            port: port,
            path: path,
            method: 'GET',
            timeout: 3000
        };
        
        const req = http.request(options, (res) => {
            resolve({
                connected: true,
                statusCode: res.statusCode,
                error: null
            });
        });
        
        req.on('error', (error) => {
            resolve({
                connected: false,
                statusCode: null,
                error: error.code === 'ECONNREFUSED' ? 'Service non démarré' : error.message
            });
        });
        
        req.on('timeout', () => {
            req.destroy();
            resolve({
                connected: false,
                statusCode: null,
                error: 'Timeout'
            });
        });
        
        req.end();
    });
}

// Exécution
testServices().catch(console.error);
import "dotenv/config";
import pg from "pg";

const { Pool } = pg;

const pool = new Pool({
    host: process.env.DB_HOST,
    port: Number(process.env.DB_PORT),
    database: process.env.DB_NAME,
    user: process.env.DB_USER,
    password: process.env.DB_PASSWORD
});
export async function conTransaccion(trabajo) {
    const cliente = await pool.connect();
    try {
        await cliente.query("BEGIN");
        const resultado = await trabajo(cliente);
        await cliente.query("COMMIT");
        return resultado;
    } catch (error) {
        await cliente.query("ROLLBACK");
        throw error;
    } finally {
        cliente.release();
    }
}

export default pool;

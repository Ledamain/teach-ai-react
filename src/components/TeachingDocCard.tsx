// TeachingDocCard.tsx
import {FileTextOutlined} from "@ant-design/icons";

const TeachingDocCard: React.FC<{
    title: string;
    onClick: () => void;
    isActive: boolean;
}> = ({ title, onClick, isActive }) => (
    <div
        onClick={onClick}
        style={{
            display: 'flex',
            alignItems: 'center',
            gap: 10,
            padding: '10px 14px',
            border: `1px solid ${isActive ? '#1677ff' : '#d9d9d9'}`,
            borderRadius: 8,
            cursor: 'pointer',
            background: isActive ? '#e6f4ff' : '#fafafa',
            maxWidth: 320,
            transition: 'all 0.2s',
        }}
    >
        <FileTextOutlined style={{ fontSize: 20, color: '#1677ff' }} />
        <div style={{ flex: 1, overflow: 'hidden' }}>
            <div style={{ fontWeight: 500, fontSize: 13, color: '#1677ff' }}>
                教案已生成
            </div>
            <div style={{
                fontSize: 12,
                color: '#888',
                whiteSpace: 'nowrap',
                overflow: 'hidden',
                textOverflow: 'ellipsis'
            }}>
                {title}
            </div>
        </div>
        <VerticalLeftOutlined style={{
            color: '#1677ff',
            transform: isActive ? 'rotate(180deg)' : 'none',
            transition: 'transform 0.2s'
        }} />
    </div>
);